package com.devwilltech.otimizacao

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devwilltech.otimizacao.data.CommandRepository
import com.devwilltech.otimizacao.data.CommandResult
import com.devwilltech.otimizacao.utils.ShizukuManager
import com.devwilltech.otimizacao.utils.PrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// ─── Firebase Realtime Database ───────────────────────────────────────────────
// Estrutura esperada em /keys/<CHAVE>:
// {
//   "active":    true,
//   "validity":  30,          // dias (0 = ilimitada)
//   "firstUsed": "",          // preenchido automaticamente na 1ª ativação
//   "deviceId":  ""           // opcional
// }
// ─────────────────────────────────────────────────────────────────────────────
private const val FIREBASE_URL  = "https://principal-6bf6f-default-rtdb.firebaseio.com"
private const val FIREBASE_API_KEY = "AIzaSyAmXzPrNaK_-Zr190oB8MuxA_sqI_ctetc"

enum class ShizukuStatus {
    CHECKING, NOT_INSTALLED, NOT_RUNNING, PERMISSION_NEEDED, READY
}

data class KeyData(
    val id: String,
    val firstUsed: String? = null,
    val validity: Int      = 0,
    val deviceId: String?  = null,
    val active: Boolean    = true
)

data class MainUiState(
    val showWelcomePopup: Boolean     = false,
    val shizukuStatus: ShizukuStatus  = ShizukuStatus.CHECKING,
    val isRunning: Boolean            = false,
    val isResetting: Boolean          = false,
    val currentCommandIndex: Int      = 0,
    val totalCommands: Int            = 0,
    val results: List<CommandResult>  = emptyList(),
    val showReport: Boolean           = false,
    val selectedCategory: String      = "Todos",
    val isHideStreamEnabled: Boolean  = false,
    val savedKey: String              = "",
    val bypassLogs: List<String>      = emptyList(),
    val isBypassing: Boolean          = false,
    val isLoggedIn: Boolean           = false,
    val keyData: KeyData?             = null,
    val loginError: String?           = null,
    val isLoggingIn: Boolean          = false,
    val isFetchingKey: Boolean        = false,
    val processingMessage: String     = "",
    val processingProgress: Float     = 0f
) {
    val canAct: Boolean
        get() = shizukuStatus == ShizukuStatus.READY && !isRunning && !isResetting
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PrefsManager(application)

    private val _uiState = MutableStateFlow(
        MainUiState(
            showWelcomePopup    = prefs.showWelcomePopup,
            isHideStreamEnabled = prefs.isHideStreamEnabled(),
            savedKey            = prefs.savedKey,
            isLoggedIn          = prefs.savedKey.isNotEmpty()
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        refreshShizukuStatus()
        if (prefs.savedKey.isNotEmpty()) {
            fetchKeyData(prefs.savedKey, recordFirstUsed = false)
        }
    }

    // ─── AUTH ─────────────────────────────────────────────────────────────────

    fun login(key: String) {
        if (key.isBlank()) return
        _uiState.update { it.copy(isLoggingIn = true, loginError = null) }
        fetchKeyData(key.trim(), recordFirstUsed = true)
    }

    fun logout() {
        prefs.savedKey = ""
        _uiState.update { it.copy(isLoggedIn = false, savedKey = "", keyData = null) }
    }

    // ─── FIREBASE KEY VALIDATION ───────────────────────────────────────────────

    private fun fetchKeyData(key: String, recordFirstUsed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isFetchingKey = true) }
            }
            try {
                // GET /keys/<KEY>.json?auth=<API_KEY>
                val endpoint = "$FIREBASE_URL/keys/$key.json?key=$FIREBASE_API_KEY"
                val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod  = "GET"
                    connectTimeout = 7000
                    readTimeout    = 7000
                    setRequestProperty("Accept", "application/json")
                }

                val code = conn.responseCode
                val body = try {
                    conn.inputStream.bufferedReader().readText()
                } catch (e: Exception) {
                    conn.errorStream?.bufferedReader()?.readText() ?: ""
                }
                conn.disconnect()

                when {
                    // ── Key não existe no banco ──────────────────────────────
                    body.trim() == "null" || code == 404 -> {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    isLoggingIn   = false,
                                    isFetchingKey = false,
                                    loginError    = "❌ Key inválida ou não encontrada."
                                )
                            }
                        }
                        return@launch
                    }

                    // ── Erro de conexão / servidor ───────────────────────────
                    code !in 200..299 -> {
                        withContext(Dispatchers.Main) {
                            _uiState.update {
                                it.copy(
                                    isLoggingIn   = false,
                                    isFetchingKey = false,
                                    loginError    = "Erro de conexão ($code). Tente novamente."
                                )
                            }
                        }
                        return@launch
                    }
                }

                val json   = JSONObject(body)
                val active = json.optBoolean("active", true)

                // ── Key bloqueada ────────────────────────────────────────────
                if (!active) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoggingIn   = false,
                                isFetchingKey = false,
                                loginError    = "🔒 Key bloqueada. Contate o suporte."
                            )
                        }
                    }
                    return@launch
                }

                // ── Primeira ativação: grava firstUsed no banco ──────────────
                var firstUsed = json.optString("firstUsed").takeIf { it.isNotBlank() }
                if (firstUsed == null && recordFirstUsed) {
                    firstUsed = System.currentTimeMillis().toString()
                    patchFirebase(key, "firstUsed", firstUsed)
                }

                val keyData = KeyData(
                    id        = key,
                    firstUsed = firstUsed,
                    validity  = json.optInt("validity", 30),
                    deviceId  = json.optString("deviceId").takeIf { it.isNotBlank() },
                    active    = true
                )

                prefs.savedKey = key
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoggedIn    = true,
                            isLoggingIn   = false,
                            isFetchingKey = false,
                            savedKey      = key,
                            keyData       = keyData,
                            loginError    = null
                        )
                    }
                }

            } catch (e: Exception) {
                // Sem internet — mantém login se já tinha key salva localmente
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoggingIn   = false,
                            isFetchingKey = false,
                            isLoggedIn    = prefs.savedKey.isNotEmpty(),
                            loginError    = if (prefs.savedKey.isEmpty())
                                "📵 Sem conexão. Verifique a internet." else null
                        )
                    }
                }
            }
        }
    }

    /** Atualiza um campo pontual no Firebase via PATCH */
    private fun patchFirebase(key: String, field: String, value: String) {
        try {
            val url  = "$FIREBASE_URL/keys/$key.json?key=$FIREBASE_API_KEY"
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod  = "PATCH"
                doOutput       = true
                connectTimeout = 5000
                readTimeout    = 5000
                setRequestProperty("Content-Type", "application/json")
            }
            conn.outputStream.use { it.write("""{"$field":"$value"}""".toByteArray()) }
            conn.responseCode
            conn.disconnect()
        } catch (_: Exception) {}
    }

    // ─── UI ───────────────────────────────────────────────────────────────────

    fun dismissWelcomePopup(dontShowAgain: Boolean) {
        if (dontShowAgain) prefs.showWelcomePopup = false
        _uiState.update { it.copy(showWelcomePopup = false) }
    }

    fun toggleHideStream(enabled: Boolean) {
        prefs.setHideStreamEnabled(enabled)
        _uiState.update { it.copy(isHideStreamEnabled = enabled) }
    }

    // ─── SHIZUKU ──────────────────────────────────────────────────────────────

    fun refreshShizukuStatus() {
        val status = when {
            !ShizukuManager.isShizukuAvailable()  -> ShizukuStatus.NOT_RUNNING
            !ShizukuManager.isPermissionGranted() -> ShizukuStatus.PERMISSION_NEEDED
            else                                  -> ShizukuStatus.READY
        }
        _uiState.update { it.copy(shizukuStatus = status) }
        if (status == ShizukuStatus.PERMISSION_NEEDED) requestShizukuPermission()
    }

    fun requestShizukuPermission() = ShizukuManager.requestPermission()

    // ─── OTIMIZAÇÃO ───────────────────────────────────────────────────────────

    fun activateAll() {
        if (_uiState.value.shizukuStatus != ShizukuStatus.READY) {
            refreshShizukuStatus(); return
        }
        val commands = CommandRepository.optimisationCommands
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRunning           = true,
                    currentCommandIndex = 0,
                    totalCommands       = commands.size,
                    results             = emptyList(),
                    showReport          = false,
                    processingMessage   = "ATIVANDO OTIMIZAÇÕES..."
                )
            }
            commands.forEachIndexed { index, cmd ->
                _uiState.update {
                    it.copy(
                        currentCommandIndex = index + 1,
                        processingProgress  = (index + 1).toFloat() / commands.size
                    )
                }
                withTimeoutOrNull(3_000L) { ShizukuManager.execute(cmd.command) }
                delay(10)
            }
            _uiState.update { it.copy(isRunning = false, showReport = true) }
        }
    }

    fun deactivateAll() {
        if (_uiState.value.shizukuStatus != ShizukuStatus.READY) {
            refreshShizukuStatus(); return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isResetting         = true,
                    currentCommandIndex = 0,
                    totalCommands       = 1,
                    showReport          = false,
                    processingMessage   = "RESTAURANDO PADRÕES..."
                )
            }
            _uiState.update { it.copy(currentCommandIndex = 1, processingProgress = 1f) }
            withTimeoutOrNull(5_000L) {
                ShizukuManager.execute(CommandRepository.resetCommand.command)
            }
            delay(500)
            _uiState.update { it.copy(isResetting = false) }
        }
    }

    // ─── BYPASS REPLAY ────────────────────────────────────────────────────────

    fun startBypass(type: String) {
        if (_uiState.value.shizukuStatus != ShizukuStatus.READY) {
            refreshShizukuStatus(); return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(isBypassing = true, bypassLogs = it.bypassLogs + "Iniciando Bypass...")
            }
            val commands = if (type == "MAX_TO_NORMAL") listOf(
                "rm -rf /data/data/com.dts.freefiremax/files/replays",
                "mkdir -p /data/data/com.dts.freefiremax/files",
                "cp -r /data/data/com.dts.freefireth/files/replays /data/data/com.dts.freefiremax/files/"
            ) else listOf(
                "rm -rf /data/data/com.dts.freefireth/files/replays",
                "mkdir -p /data/data/com.dts.freefireth/files",
                "cp -r /data/data/com.dts.freefiremax/files/replays /data/data/com.dts.freefireth/files/"
            )
            commands.forEach { cmd ->
                withTimeoutOrNull(5_000L) { ShizukuManager.execute(cmd) }
                _uiState.update { it.copy(bypassLogs = it.bypassLogs + cmd) }
                delay(200)
            }
            _uiState.update {
                it.copy(isBypassing = false, bypassLogs = it.bypassLogs + "Bypass Concluído!")
            }
        }
    }

    fun clearBypassLogs() = _uiState.update { it.copy(bypassLogs = emptyList()) }
    fun dismissReport()   = _uiState.update { it.copy(showReport  = false) }
}

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

// ─────────────────────────────────────────────────────────────────────────────
// ⚠️  Substitua pela URL do seu Firebase Realtime Database
//     Formato: https://SEU-PROJETO-default-rtdb.firebaseio.com
//
//     A estrutura esperada no banco:
//     {
//       "keys": {
//         "SUACHAVE123": {
//           "firstUsed": "1700000000000",   ← timestamp ms quando usou pela 1ª vez
//           "validity": 30,                 ← dias de validade (0 = ilimitado)
//           "deviceId": "abc123",           ← opcional
//           "active": true                  ← false = key bloqueada
//         }
//       }
//     }
// ─────────────────────────────────────────────────────────────────────────────
private const val FIREBASE_URL = "https://SEU-PROJETO-default-rtdb.firebaseio.com"

// Se o banco exigir autenticação (rules: auth != null), gere um token de serviço
// e cole aqui. Se as rules estiverem abertas para leitura, deixe vazio.
private const val FIREBASE_AUTH_TOKEN = ""

enum class ShizukuStatus {
    CHECKING, NOT_INSTALLED, NOT_RUNNING, PERMISSION_NEEDED, READY
}

data class KeyData(
    val id: String,
    val firstUsed: String? = null,
    val validity: Int      = 0,       // 0 = ilimitada
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

    /**
     * Busca a key no Firebase Realtime Database via REST.
     *
     * [recordFirstUsed] = true → se firstUsed estiver vazio no banco,
     *   grava o timestamp atual (primeira ativação).
     */
    private fun fetchKeyData(key: String, recordFirstUsed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isFetchingKey = true) }
            }
            try {
                // ── 1. Buscar dados da key ──────────────────────────────────
                val authParam = if (FIREBASE_AUTH_TOKEN.isNotEmpty())
                    "?auth=$FIREBASE_AUTH_TOKEN" else ".json"
                val endpoint  = if (FIREBASE_AUTH_TOKEN.isNotEmpty())
                    "$FIREBASE_URL/keys/$key.json?auth=$FIREBASE_AUTH_TOKEN"
                else
                    "$FIREBASE_URL/keys/$key.json"

                val url  = URL(endpoint)
                val conn = (url.openConnection() as HttpURLConnection).also {
                    it.requestMethod  = "GET"
                    it.connectTimeout = 6000
                    it.readTimeout    = 6000
                    it.setRequestProperty("Accept", "application/json")
                }

                val code = conn.responseCode
                if (code != 200) {
                    val msg = when (code) {
                        401, 403 -> "Sem permissão no banco"
                        404      -> "Key não encontrada"
                        else     -> "Erro de conexão ($code)"
                    }
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoggingIn  = false,
                                isFetchingKey = false,
                                loginError   = msg
                            )
                        }
                    }
                    conn.disconnect()
                    return@launch
                }

                val body = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                // Firebase retorna "null" (string) quando a key não existe
                if (body.trim() == "null") {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoggingIn   = false,
                                isFetchingKey = false,
                                loginError    = "Key inválida ou não encontrada"
                            )
                        }
                    }
                    return@launch
                }

                val json = JSONObject(body)

                // ── 2. Verificar se está ativa ──────────────────────────────
                val active = json.optBoolean("active", true)
                if (!active) {
                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                isLoggingIn   = false,
                                isFetchingKey = false,
                                loginError    = "Key bloqueada. Contate o suporte."
                            )
                        }
                    }
                    return@launch
                }

                // ── 3. Gravar firstUsed se for primeira ativação ────────────
                var firstUsedVal = json.optString("firstUsed").takeIf { it.isNotBlank() }

                if (firstUsedVal == null && recordFirstUsed) {
                    firstUsedVal = System.currentTimeMillis().toString()
                    // PATCH: grava firstUsed no banco
                    patchFirebase(key, "firstUsed", firstUsedVal)
                }

                val keyData = KeyData(
                    id        = key,
                    firstUsed = firstUsedVal,
                    validity  = json.optInt("validity", 30),
                    deviceId  = json.optString("deviceId").takeIf { it.isNotBlank() },
                    active    = true
                )

                // ── 4. Salvar key localmente e atualizar estado ─────────────
                withContext(Dispatchers.Main) {
                    prefs.savedKey = key
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
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoggingIn   = false,
                            isFetchingKey = false,
                            // Sem internet: mantém login se já tinha key salva
                            isLoggedIn    = prefs.savedKey.isNotEmpty(),
                            loginError    = if (prefs.savedKey.isEmpty())
                                "Sem conexão. Verifique a internet." else null
                        )
                    }
                }
            }
        }
    }

    /** Atualiza um campo no Firebase via PATCH REST */
    private fun patchFirebase(key: String, field: String, value: String) {
        try {
            val endpoint = if (FIREBASE_AUTH_TOKEN.isNotEmpty())
                "$FIREBASE_URL/keys/$key.json?auth=$FIREBASE_AUTH_TOKEN"
            else
                "$FIREBASE_URL/keys/$key.json"

            val url  = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).also {
                it.requestMethod     = "PATCH"
                it.doOutput          = true
                it.connectTimeout    = 5000
                it.readTimeout       = 5000
                it.setRequestProperty("Content-Type", "application/json")
            }
            val body = """{"$field":"$value"}"""
            conn.outputStream.use { it.write(body.toByteArray()) }
            conn.responseCode // dispara a requisição
            conn.disconnect()
        } catch (_: Exception) { /* silencioso */ }
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
                // ⚡ Timeout por comando — impede travamento em 88%
                withTimeoutOrNull(3_000L) {
                    ShizukuManager.execute(cmd.command)
                }
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

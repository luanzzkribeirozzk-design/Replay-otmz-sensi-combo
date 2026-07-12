package com.devwilltech.otimizacao.utils

import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuRemoteProcess

object ShizukuManager {

    private const val SHIZUKU_PERMISSION_CODE = 1001

    fun isShizukuAvailable(): Boolean = try {
        Shizuku.pingBinder()
    } catch (e: Exception) {
        false
    }

    fun isPermissionGranted(): Boolean = try {
        when {
            Shizuku.isPreV11() -> true
            else -> Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }
    } catch (e: Exception) {
        false
    }

    fun requestPermission() {
        try {
            if (!Shizuku.isPreV11()) {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_CODE)
            }
        } catch (_: Exception) {}
    }

    fun addRequestPermissionResultListener(l: Shizuku.OnRequestPermissionResultListener) {
        try { Shizuku.addRequestPermissionResultListener(l) } catch (_: Exception) {}
    }

    fun removeRequestPermissionResultListener(l: Shizuku.OnRequestPermissionResultListener) {
        try { Shizuku.removeRequestPermissionResultListener(l) } catch (_: Exception) {}
    }

    fun addBinderReceivedListener(l: Shizuku.OnBinderReceivedListener) {
        try { Shizuku.addBinderReceivedListener(l) } catch (_: Exception) {}
    }

    fun removeBinderReceivedListener(l: Shizuku.OnBinderReceivedListener) {
        try { Shizuku.removeBinderReceivedListener(l) } catch (_: Exception) {}
    }

    fun addBinderDeadListener(l: Shizuku.OnBinderDeadListener) {
        try { Shizuku.addBinderDeadListener(l) } catch (_: Exception) {}
    }

    fun removeBinderDeadListener(l: Shizuku.OnBinderDeadListener) {
        try { Shizuku.removeBinderDeadListener(l) } catch (_: Exception) {}
    }

    suspend fun execute(command: String): CommandExecResult = withContext(Dispatchers.IO) {
        try {
            // Usando reflexão para acessar o método newProcess que está marcado como privado/internal na biblioteca
            val newProcessMethod = Shizuku::class.java.getDeclaredMethod(
                "newProcess",
                Array<String>::class.java,
                Array<String>::class.java,
                String::class.java
            )
            newProcessMethod.isAccessible = true
            
            val process = newProcessMethod.invoke(
                null,
                arrayOf("sh", "-c", command),
                null,
                null
            ) as ShizukuRemoteProcess

            val stdout   = process.inputStream.bufferedReader().readText().trim()
            val stderr   = process.errorStream.bufferedReader().readText().trim()
            val exitCode = process.waitFor()

            CommandExecResult(
                success  = exitCode == 0,
                output   = stdout.ifBlank { if (exitCode == 0) "OK" else stderr },
                exitCode = exitCode
            )

        } catch (e: SecurityException) {
            CommandExecResult(
                success  = false,
                output   = "Permissão negada — autorize o app no Shizuku",
                exitCode = -1
            )
        } catch (e: Exception) {
            CommandExecResult(
                success  = false,
                output   = "Erro: ${e.message ?: "desconhecido"}",
                exitCode = -1
            )
        }
    }
}

data class CommandExecResult(
    val success:  Boolean,
    val output:   String,
    val exitCode: Int
)

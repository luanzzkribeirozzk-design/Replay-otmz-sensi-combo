package com.devwilltech.otimizacao.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.devwilltech.otimizacao.MainViewModel
import com.devwilltech.otimizacao.ui.theme.NeonYellow

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SensiScreen(viewModel: MainViewModel) {
    val context   = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { ctx ->
                WebView(ctx).apply {
                    with(settings) {
                        javaScriptEnabled        = true
                        domStorageEnabled        = true
                        allowFileAccess          = false
                        allowContentAccess       = false
                        // Desabilita acesso cross-origin desnecessário
                        @Suppress("DEPRECATION")
                        allowFileAccessFromFileURLs    = false
                        @Suppress("DEPRECATION")
                        allowUniversalAccessFromFileURLs = false
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }
                    }

                    // ⚡ FIX: carrega o HTML dos assets com base URL HTTPS.
                    // loadUrl("file://...") bloqueava crypto.subtle no Android.
                    // loadDataWithBaseURL com "https://localhost/" cria contexto
                    // seguro sem precisar da dependência androidx.webkit.
                    try {
                        val html = ctx.assets.open("sensi.html")
                            .bufferedReader()
                            .readText()

                        loadDataWithBaseURL(
                            "https://localhost/",  // base URL → habilita crypto.subtle
                            html,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    } catch (e: Exception) {
                        loadData(
                            "<html><body style=\'background:#000;color:#FF453A;font-family:monospace;padding:24px\'>" +
                            "<h3>Erro ao carregar sensi.html</h3><p>${e.message}</p></body></html>",
                            "text/html",
                            "UTF-8"
                        )
                        isLoading = false
                    }
                }
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier    = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color       = NeonYellow,
                strokeWidth = 3.dp
            )
        }
    }
}

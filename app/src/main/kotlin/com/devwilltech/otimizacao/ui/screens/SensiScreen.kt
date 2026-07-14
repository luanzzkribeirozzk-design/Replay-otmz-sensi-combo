package com.devwilltech.otimizacao.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
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
import androidx.webkit.WebViewAssetLoader
import com.devwilltech.otimizacao.MainViewModel
import com.devwilltech.otimizacao.ui.theme.NeonYellow

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SensiScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->

                // ⚡ FIX: WebViewAssetLoader serve os assets via
                // https://appassets.androidplatform.net → crypto.subtle funciona.
                // Carregando como file:// bloqueava a Web Crypto API no Android.
                val assetLoader = WebViewAssetLoader.Builder()
                    .setDomain("appassets.androidplatform.net")
                    .addPathHandler(
                        "/assets/",
                        WebViewAssetLoader.AssetsPathHandler(ctx)
                    )
                    .build()

                WebView(ctx).apply {
                    with(settings) {
                        javaScriptEnabled              = true
                        domStorageEnabled              = true
                        allowFileAccess                = false
                        allowContentAccess             = false
                        @Suppress("DEPRECATION")
                        allowFileAccessFromFileURLs    = false
                        @Suppress("DEPRECATION")
                        allowUniversalAccessFromFileURLs = false
                        // Garante que o WebView use a versão mais moderna disponível
                        mediaPlaybackRequiresUserGesture = false
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldInterceptRequest(
                            view: WebView,
                            request: WebResourceRequest
                        ): WebResourceResponse? {
                            // Redireciona requisições para os assets locais
                            return assetLoader.shouldInterceptRequest(request.url)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }
                    }

                    // Carrega via HTTPS sintético — habilita crypto.subtle
                    loadUrl("https://appassets.androidplatform.net/assets/sensi.html")
                }
            }
        )

        // Spinner enquanto a página carrega / descriptografa
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color = NeonYellow,
                strokeWidth = 3.dp
            )
        }
    }
}

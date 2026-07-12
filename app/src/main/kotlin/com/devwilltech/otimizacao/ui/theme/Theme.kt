package com.devwilltech.otimizacao.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonYellow,
    secondary = NeonYellowSoft,
    tertiary = SuccessGreen,
    background = BackgroundDeep,
    surface = BackgroundCard,
    onPrimary = BackgroundDeep,
    onSecondary = TextPrimary,
    onTertiary = BackgroundDeep,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun OtimizacaoWillTechTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundDeep.toArgb()
            window.navigationBarColor = BackgroundDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

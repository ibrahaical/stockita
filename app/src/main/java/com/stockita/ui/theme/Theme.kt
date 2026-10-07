package com.stockita.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Primary600,
    onPrimary = Color.White,
    primaryContainer = Primary100,
    onPrimaryContainer = Primary800,
    background = BgApp,
    onBackground = TextPrimary,
    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    outlineVariant = BorderStrong,
    error = Danger,
    onError = Color.White,
    errorContainer = DangerBg,
    onErrorContainer = DangerText
)

@Composable
fun StockitaTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                // Pastikan indikator sistem (baterai, jam, wifi, sinyal) berwarna gelap di atas surface terang
                insetsController.isAppearanceLightStatusBars = true
                insetsController.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}

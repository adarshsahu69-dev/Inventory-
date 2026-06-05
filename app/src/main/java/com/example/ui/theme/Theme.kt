package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Slate800 = Color(0xFF1E293B)
val Slate900 = Color(0xFF0F172A)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Emerald500 = Color(0xFF10B981)
val Emerald600 = Color(0xFF059669)
val Red500 = Color(0xFFEF4444)

private val DarkColorScheme = darkColorScheme(
    primary = Emerald500,
    secondary = Slate200,
    tertiary = Emerald600,
    background = Slate900,
    surface = Slate800,
    onPrimary = Color.White,
    onSecondary = Slate900,
    onTertiary = Color.White,
    onBackground = Slate100,
    onSurface = Slate100,
    error = Red500,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald600,
    secondary = Slate800,
    tertiary = Emerald500,
    background = Slate100,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Slate900,
    onSurface = Slate900,
    error = Red500,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // disabled for explicit slate/emerald design
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

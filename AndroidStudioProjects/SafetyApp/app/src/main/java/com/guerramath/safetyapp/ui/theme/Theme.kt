package com.guerramath.safetyapp.ui.theme

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

// 1. Definição de Cores Neo-Brutalistas (Faltava isso)
private val DarkColorScheme = darkColorScheme(
    primary = Color.Black,          // Base do Neo-brutalismo
    secondary = Color(0xFF00FFFF),  // Cyan (Safety/Aviation)
    tertiary = Color.White,
    background = Color(0xFF0D1B2A), // Fundo Navy Profundo
    surface = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    secondary = Color(0xFF008B8B),
    tertiary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White
)

@Composable
fun SafetyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Para o Neo-brutalismo da Sentra, recomendamos deixar 'false' por padrão
    // para não perder a identidade visual da marca para as cores do papel de parede do usuário.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) { // Checagem de segurança (SOP)
                val window = context.window
                window.statusBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Certifique-se que o arquivo Type.kt existe na mesma pasta
        content = content
    )
}
package com.practicum.vkproject3.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    surface = DarkGreen
)

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = Color.White
)

@Composable
fun VkProject3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Отключаем динамические цвета, чтобы наши кастомные цвета работали везде
    dynamicColor: Boolean = false,
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
            // Красим статус-бар под цвет фона
            window.statusBarColor = BeigeBackground.toArgb()
            // Делаем иконки (часы, уведомления) темными, так как фон светлый
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
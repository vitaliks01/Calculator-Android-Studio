package com.example.lab2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),       // Зелений
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFA5D6A7),

    secondary = Color(0xFF81C784),     // Світло-зелений
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF388E3C),
    onSecondaryContainer = Color(0xFFC8E6C9),

    background = Color(0xFF0A1F0A),    // Темно-зелений фон
    onBackground = Color(0xFFE8F5E8),
    surface = Color(0xFF1B3B1B),       // Картки
    onSurface = Color(0xFFE8F5E8),
    surfaceVariant = Color(0xFF2D4F2D), // Поля вводу
    onSurfaceVariant = Color(0xFFC8E6C9)
)

// Світла версія (опційно)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF000000),

    secondary = Color(0xFF388E3C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF000000),

    background = Color(0xFFF5FBF5),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFE8F5E9),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFDCEDC8),
    onSurfaceVariant = Color(0xFF000000)
)

@Composable
fun Lab2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
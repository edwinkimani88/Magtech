package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MagTechTealLight,
    onPrimary = Color.Black,
    primaryContainer = MagTechTealDark,
    onPrimaryContainer = MagTechTealLight,
    secondary = MagTechAccentGold,
    onSecondary = Color.Black,
    background = MagTechBackgroundDark,
    onBackground = MagTechOnSurfaceDark,
    surface = MagTechSurfaceDark,
    onSurface = MagTechOnSurfaceDark,
    error = MagTechStatusRed
)

private val LightColorScheme = lightColorScheme(
    primary = MagTechTealPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = MagTechTealDark,
    secondary = MagTechAccentGold,
    onSecondary = Color.White,
    background = MagTechBackgroundLight,
    onBackground = MagTechOnSurfaceLight,
    surface = MagTechSurfaceLight,
    onSurface = MagTechOnSurfaceLight,
    error = MagTechStatusRed
)

@Composable
fun MagTechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MagTechTheme(darkTheme = darkTheme, content = content)
}

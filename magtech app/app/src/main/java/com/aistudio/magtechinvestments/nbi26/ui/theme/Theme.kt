package com.aistudio.magtechinvestments.nbi26.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MagTechColorScheme = darkColorScheme(
    primary = TerracottaPeach,
    onPrimary = TextOnTerracotta,
    primaryContainer = TerracottaDark,
    onPrimaryContainer = Color.White,
    secondary = TerracottaLight,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    error = AccentRed,
    onError = Color.White
)

@Composable
fun MagTechTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MagTechColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MagTechTheme(content = content)
}

@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TerracottaPeach,
    unfocusedBorderColor = DarkBorder,
    focusedLabelColor = TerracottaPeach,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)

package com.example.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,

    secondary = PurplePrimaryLight,
    onSecondary = Color.White,

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,

    surfaceVariant = SurfaceVariant,
    outline = Outline,

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun BankTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = BankTypography,
        content = content
    )
}

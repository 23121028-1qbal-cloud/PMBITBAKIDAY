package com.example.pmbitbakiday.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PmbDarkColorScheme = darkColorScheme(
    primary = PastelYellow,
    onPrimary = TextOnBrightAccent,
    secondary = LimePastel,
    onSecondary = TextOnBrightAccent,
    tertiary = CoralRed,
    onTertiary = TextPrimaryOnDark,
    background = CharcoalBackground,
    onBackground = TextPrimaryOnDark,
    surface = SurfacePrimary,
    onSurface = TextPrimaryOnDark,
    surfaceVariant = SurfaceSecondary,
    onSurfaceVariant = TextSecondaryOnDark,
    outline = BorderDark,
    error = StatusError,
    onError = TextPrimaryOnDark
)

@Composable
fun PmbTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PmbDarkColorScheme,
        content = content
    )
}

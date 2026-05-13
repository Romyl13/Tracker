package com.healthtracker.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Mint,
    onPrimary = Background,
    secondary = MintDim,
    onSecondary = OnDark,
    background = Background,
    onBackground = OnDark,
    surface = Surface,
    onSurface = OnDark,
    error = RelapseRed,
    onError = OnDark
)

@Composable
fun HealthTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = HealthTypography,
        content = content
    )
}

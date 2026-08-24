package com.clickbus.challenge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClickBusColorScheme = darkColorScheme(
    primary = PurpleBrand,
    onPrimary = Color.White,
    secondary = Purple400,
    background = SurfaceBgAlt,
    surface = SurfaceWhite,
    onBackground = InkDark,
    onSurface = InkDark,
    error = AccentRed,
)

@Composable
fun ClickBusChallengeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ClickBusColorScheme,
        typography = ClickBusTypography,
        content = content,
    )
}

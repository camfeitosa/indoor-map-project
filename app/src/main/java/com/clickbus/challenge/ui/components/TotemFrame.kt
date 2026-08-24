package com.clickbus.challenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.ui.theme.Purple300
import com.clickbus.challenge.ui.theme.Purple700

/** Kiosk (Totem) shell wrapper: purple bezel frame + white rounded screen. */
@Composable
fun TotemFrame(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Purple700, Purple300, Purple700)))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F5FA), RoundedCornerShape(32.dp)),
        ) {
            content()
        }
    }
}

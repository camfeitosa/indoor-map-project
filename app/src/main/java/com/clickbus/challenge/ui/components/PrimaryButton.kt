package com.clickbus.challenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.Purple400
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextFaint

/** Solid/gradient primary call-to-action button used across the flow. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradient: Brush = Brush.horizontalGradient(listOf(PurpleBrand, Purple400)),
) {
    val background = if (enabled) gradient else Brush.horizontalGradient(listOf(BorderLight, BorderLight))
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(background, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleSmall, color = if (enabled) Color.White else TextFaint)
    }
}

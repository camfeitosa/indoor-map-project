package com.clickbus.challenge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.TextMuted
import com.clickbus.challenge.ui.theme.TextSecondary
import kotlin.random.Random

/**
 * "Levar rota para meu celular" — QR handoff modal shown over [TotemMapScreen].
 * The grid pattern is generated (not a scannable code) purely to reproduce the visual design.
 */
@Composable
fun TotemQrCodeDialog(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .background(Color.White, RoundedCornerShape(14.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Levar rota para meu celular",
                style = MaterialTheme.typography.titleMedium,
                color = InkDark,
                textAlign = TextAlign.Center,
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFF0F172B), RoundedCornerShape(9.dp))
                    .padding(18.dp),
            ) {
                QrPattern()
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(20.dp))

            Text(
                text = "Escaneie o QR Code com a câmera do seu celular",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "A rota será aberta no app/navegador",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172B), RoundedCornerShape(8.dp))
                    .clickable(onClick = onClose)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Fechar", color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun QrPattern() {
    val cells = remember { List(64) { Random(it).nextBoolean() } }
    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        userScrollEnabled = false,
    ) {
        items(cells) { filled ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(if (filled) Color.White else Color.Transparent, RoundedCornerShape(2.dp)),
            )
        }
    }
}

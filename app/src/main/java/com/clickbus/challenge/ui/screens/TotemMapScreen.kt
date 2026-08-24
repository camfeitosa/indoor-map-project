package com.clickbus.challenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.R
import com.clickbus.challenge.model.RouteStep
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.components.TotemFrame
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.Purple300
import com.clickbus.challenge.ui.theme.PurpleBrand

private val kioskRouteSteps = listOf(
    RouteStep(1, "Vire à esquerda"),
    RouteStep(2, "Siga pelo corredor ao lado das lanchonetes"),
    RouteStep(3, "Os banheiros estarão à sua direita"),
)

/** Totem — indoor map with the traced route, ending in a "send route to my phone" action. */
@Composable
fun TotemMapScreen(
    onBack: () -> Unit,
    onRouteSentToPhone: () -> Unit,
) {
    var showQrDialog by remember { mutableStateOf(false) }

    StatusBarIcons(darkIcons = false)

    TotemFrame {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = InkDark, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.clickbus_logo_white),
                    contentDescription = "ClickBus",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(30.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(40.dp))
            }

            Box(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFFF8FAFC)),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.indoor_map_illustration),
                    contentDescription = "Mapa da rodoviária com rota traçada",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Purple300, RoundedCornerShape(9.dp))
                        .padding(11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Filled.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Vire à esquerda e siga pelo corredor ao lado das lanchonetes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                    )
                }

                kioskRouteSteps.forEach { step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(9.dp))
                            .padding(9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Purple300, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = step.index.toString(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(text = step.instruction, style = MaterialTheme.typography.bodySmall, color = InkDark)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF7D4AC1), RoundedCornerShape(9.dp))
                        .clickable { showQrDialog = true }
                        .padding(11.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.QrCode2, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Levar rota para meu celular", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }
        }
    }

    if (showQrDialog) {
        TotemQrCodeDialog(
            onClose = {
                showQrDialog = false
                onRouteSentToPhone()
            },
        )
    }
}

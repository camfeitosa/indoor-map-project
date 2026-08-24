package com.clickbus.challenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.R
import com.clickbus.challenge.model.StationPlace
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.components.TotemFrame
import com.clickbus.challenge.ui.theme.AccentAmber
import com.clickbus.challenge.ui.theme.AccentGreen
import com.clickbus.challenge.ui.theme.AccentIndigo
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.Purple200
import com.clickbus.challenge.ui.theme.Purple50
import com.clickbus.challenge.ui.theme.Purple800
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextFaint

private val kioskServices = listOf(
    StationPlace("Piso de Alimentação", "Piso 1 · Ala B", Icons.Filled.Restaurant, AccentAmber, AccentAmber.copy(alpha = 0.13f)),
    StationPlace("Bilheteria", "Entrada principal", Icons.Filled.ConfirmationNumber, AccentGreen, AccentGreen.copy(alpha = 0.13f)),
    StationPlace("Guarda-volumes", "Plataforma 08 · Ala A", Icons.Filled.Inventory2, AccentIndigo, AccentIndigo.copy(alpha = 0.13f)),
)

private val kioskPlatforms = (1..12).map { it.toString() to "Plataforma $it" }

/** Totem — "Para onde deseja ir?": services list plus boarding platforms. */
@Composable
fun TotemServicesScreen(
    onBack: () -> Unit,
    onSelectPlatform: (String, String) -> Unit,
) {
    StatusBarIcons(darkIcons = false)

    var selectedService by remember { mutableStateOf<String?>(null) }
    var selectedPlatform by remember { mutableStateOf<String?>(null) }

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

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(text = "Para onde deseja ir?", style = MaterialTheme.typography.titleSmall, color = InkDark)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "SERVIÇOS", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                }

                items(kioskServices) { place ->
                    com.clickbus.challenge.ui.components.StationPlaceRow(
                        place = place,
                        selected = selectedService == place.title,
                        onClick = { selectedService = place.title },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "PLATAFORMAS DE EMBARQUE", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                }

                items(kioskPlatforms) { (number, label) ->
                    val selected = selectedPlatform == number
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (selected) Purple50 else Color.White, RoundedCornerShape(10.dp))
                            .border(1.dp, Purple200, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedPlatform = number
                                onSelectPlatform(number, label)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(27.dp)
                                .background(PurpleBrand, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = number, color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Purple800)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

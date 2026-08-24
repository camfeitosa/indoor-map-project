package com.clickbus.challenge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.model.StationPlace
import com.clickbus.challenge.ui.components.AppBottomNavBar
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.components.PrimaryButton
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.AccentAmber
import com.clickbus.challenge.ui.theme.AccentGreen
import com.clickbus.challenge.ui.theme.AccentIndigo
import com.clickbus.challenge.ui.theme.AccentRed
import com.clickbus.challenge.ui.theme.Purple400
import com.clickbus.challenge.ui.theme.Purple600
import com.clickbus.challenge.ui.theme.Purple700
import com.clickbus.challenge.ui.theme.Purple100
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.SurfaceBgAlt
import com.clickbus.challenge.ui.theme.TextFaint

private val popularDestinations = listOf(
    StationPlace("Plataforma 32", "Ala C · Piso térreo", Icons.Filled.LocationOn, PurpleBrand, Purple100),
    StationPlace("Plataforma 08", "Ala A · Piso térreo", Icons.Filled.LocationOn, PurpleBrand, Purple100),
    StationPlace("Piso de Alimentação", "Piso 1 · Ala B", Icons.Filled.Restaurant, AccentAmber, AccentAmber.copy(alpha = 0.13f)),
    StationPlace("Bilheteria", "Entrada principal", Icons.Filled.ConfirmationNumber, AccentGreen, AccentGreen.copy(alpha = 0.13f)),
    StationPlace("Farmácia", "Piso térreo · Ala A", Icons.Filled.LocalHospital, AccentRed, AccentRed.copy(alpha = 0.13f)),
    StationPlace("Guarda-volumes", "Plataforma 08 · Ala A", Icons.Filled.Inventory2, AccentIndigo, AccentIndigo.copy(alpha = 0.13f)),
    StationPlace("Banheiros", "Todos os pisos", Icons.Filled.Wc, androidx.compose.ui.graphics.Color(0xFF0EA5E9), androidx.compose.ui.graphics.Color(0xFF0EA5E9).copy(alpha = 0.13f)),
)

/** "Buscar destino" — search bar plus a list of popular destinations/services. */
@Composable
fun SearchDestinationScreen(
    onBack: () -> Unit,
    onDestinationSelected: (StationPlace) -> Unit,
    onTraceRoute: () -> Unit,
    onSelectTab: (AppTab) -> Unit = {},
) {
    StatusBarIcons(darkIcons = false)

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Purple700, Purple600, Purple400)))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Buscar destino", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = TextFaint, modifier = Modifier.size(16.dp))
                Text(text = "Para onde você vai?", style = MaterialTheme.typography.bodyMedium, color = TextFaint)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .background(SurfaceBgAlt)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                text = "DESTINOS POPULARES",
                style = MaterialTheme.typography.labelSmall,
                color = TextFaint,
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(popularDestinations) { place ->
                    com.clickbus.challenge.ui.components.StationPlaceRow(
                        place = place,
                        onClick = { onDestinationSelected(place) },
                    )
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                PrimaryButton(text = "Traçar rota →", onClick = onTraceRoute)
            }
            AppBottomNavBar(selected = AppTab.Buscar, onSelect = onSelectTab)
        }
    }
}

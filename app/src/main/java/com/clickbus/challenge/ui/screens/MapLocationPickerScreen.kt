package com.clickbus.challenge.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.West
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.model.StationPlace
import com.clickbus.challenge.ui.components.AppBottomNavBar
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.components.PrimaryButton
import com.clickbus.challenge.ui.components.StationPlaceRow
import com.clickbus.challenge.ui.components.StationTopBar
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.AccentAmber
import com.clickbus.challenge.ui.theme.AccentGreen
import com.clickbus.challenge.ui.theme.AccentIndigo
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.Purple100
import com.clickbus.challenge.ui.theme.Purple200
import com.clickbus.challenge.ui.theme.Purple50
import com.clickbus.challenge.ui.theme.Purple800
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.SurfaceBgAlt
import com.clickbus.challenge.ui.theme.TextFaint
import com.clickbus.challenge.ui.theme.TextSecondary

private data class EntryOption(val label: String, val icon: ImageVector)

private val entryOptions = listOf(
    EntryOption("Acesso Metrô / CPTM", Icons.Filled.DirectionsSubway),
    EntryOption("Entrada Principal (Norte)", Icons.Filled.ArrowDownward),
    EntryOption("Entrada Leste", Icons.Filled.West),
    EntryOption("Táxi / Aplicativos", Icons.Filled.LocalTaxi),
)

private const val QR_ORIGIN_LABEL = "QR Code escaneado"

private val nearbyServices = listOf(
    StationPlace("Piso de Alimentação", "Piso 1 · Ala B", Icons.Filled.Restaurant, AccentAmber, AccentAmber.copy(alpha = 0.13f)),
    StationPlace("Bilheteria", "Entrada principal", Icons.Filled.ConfirmationNumber, AccentGreen, AccentGreen.copy(alpha = 0.13f)),
    StationPlace("Guarda-volumes", "Plataforma 08 · Ala A", Icons.Filled.Inventory2, AccentIndigo, AccentIndigo.copy(alpha = 0.13f)),
)

private val allPlatforms = (1..12).map { it.toString() }

/** "Onde você está agora?" — choose current location and destination, then trace the route. */
@Composable
fun MapLocationPickerScreen(
    onBack: () -> Unit,
    onGoToMap: (origin: String, destination: String) -> Unit,
    preselectedDestination: String? = null,
    onSelectTab: (AppTab) -> Unit = {},
) {
    StatusBarIcons(darkIcons = true)

    var origin by remember { mutableStateOf<String?>(null) }
    var destination by remember { mutableStateOf(preselectedDestination) }
    var showQrScanner by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            StationTopBar(title = "Rodoviária Tietê", subtitle = "Navegação interna", onBack = onBack)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(SurfaceBgAlt)
                    .padding(horizontal = 13.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(text = "Onde você está agora?", style = MaterialTheme.typography.titleSmall, color = InkDark)
                }

                item {
                    SelectableRow(
                        label = "Escanear QR Code",
                        icon = Icons.Filled.QrCodeScanner,
                        selected = origin == QR_ORIGIN_LABEL,
                        highlightWhenUnselected = true,
                        onClick = { showQrScanner = true },
                    )
                }

                items(entryOptions) { option ->
                    SelectableRow(
                        label = option.label,
                        icon = option.icon,
                        selected = origin == option.label,
                        onClick = { origin = option.label },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Para onde deseja ir?", style = MaterialTheme.typography.titleSmall, color = InkDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "SERVIÇOS", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                }

                items(nearbyServices) { place ->
                    StationPlaceRow(
                        place = place,
                        selected = destination == place.title,
                        onClick = { destination = place.title },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "PLATAFORMAS DE EMBARQUE", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                    Spacer(modifier = Modifier.height(8.dp))

                    PlatformGrid(
                        platforms = allPlatforms,
                        selected = destination,
                        onSelect = { number -> destination = "Plataforma $number" },
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    PrimaryButton(
                        text = "Ir para o mapa →",
                        enabled = origin != null && destination != null,
                        onClick = {
                            val selectedOrigin = origin
                            val selectedDestination = destination
                            if (selectedOrigin != null && selectedDestination != null) {
                                onGoToMap(selectedOrigin, selectedDestination)
                            }
                        },
                    )
                }
                AppBottomNavBar(selected = AppTab.Buscar, onSelect = onSelectTab)
            }
        }

        if (showQrScanner) {
            QrScanOverlay(
                onScanned = {
                    origin = QR_ORIGIN_LABEL
                    showQrScanner = false
                },
                onClose = { showQrScanner = false },
            )
        }
    }
}

@Composable
private fun SelectableRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    highlightWhenUnselected: Boolean = false,
) {
    val unselectedEmphasis = highlightWhenUnselected && !selected
    val background = when {
        selected -> Purple50
        unselectedEmphasis -> PurpleBrand
        else -> Color.White
    }
    val border = when {
        selected -> Purple200
        unselectedEmphasis -> PurpleBrand
        else -> BorderLight
    }
    val iconCircleBackground = if (selected) PurpleBrand else if (unselectedEmphasis) Color.White.copy(alpha = 0.2f) else SurfaceBgAlt
    val iconTint = if (selected || unselectedEmphasis) Color.White else TextSecondary
    val textColor = if (selected) Purple800 else if (unselectedEmphasis) Color.White else Color(0xFF364153)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(12.dp))
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .size(33.dp)
                .background(iconCircleBackground, CircleShape),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (selected) Icons.Filled.Check else icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = textColor)
    }
}

@Composable
private fun PlatformGrid(
    platforms: List<String>,
    selected: String?,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        platforms.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { number ->
                    val isSelected = selected == "Plataforma $number"
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isSelected) PurpleBrand else Color.White, RoundedCornerShape(10.dp))
                            .border(1.dp, if (isSelected) PurpleBrand else BorderLight, RoundedCornerShape(10.dp))
                            .clickable { onSelect(number) }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = number,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Color.White else InkDark,
                        )
                    }
                }
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

package com.clickbus.challenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.R
import com.clickbus.challenge.model.RouteStep
import com.clickbus.challenge.ui.components.AppBottomNavBar
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.components.StationTopBar
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.SurfaceBgAlt

private val defaultRouteSteps = listOf(
    RouteStep(1, "Vire à esquerda"),
    RouteStep(2, "Siga pelo corredor ao lado das lanchonetes"),
    RouteStep(3, "Os banheiros estarão à sua direita"),
)

/** The indoor map with the traced route and turn-by-turn instructions. */
@Composable
fun MapRouteScreen(
    destinationLabel: String,
    onBack: () -> Unit,
    onSelectTab: (AppTab) -> Unit,
    steps: List<RouteStep> = defaultRouteSteps,
) {
    StatusBarIcons(darkIcons = true)

    Column(modifier = Modifier.fillMaxSize()) {
        StationTopBar(title = "Rodoviária Tietê", subtitle = destinationLabel, onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .background(SurfaceBgAlt),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(14.dp),
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
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleBrand, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(Icons.Filled.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                    Text(
                        text = steps.firstOrNull()?.let { "Vire à esquerda e siga pelo corredor ao lado das lanchonetes" }
                            ?: "Você chegou ao seu destino",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                    )
                }

                steps.forEach { step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(PurpleBrand, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = step.index.toString(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(text = step.instruction, style = MaterialTheme.typography.bodySmall, color = InkDark)
                    }
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(14.dp))
        }

        AppBottomNavBar(selected = AppTab.Buscar, onSelect = onSelectTab)
    }
}

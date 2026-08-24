package com.clickbus.challenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextFaint

enum class StationTab(val label: String, val icon: ImageVector) {
    Inicio("Início", Icons.Filled.Home),
    Buscar("Buscar", Icons.Filled.Search),
    Mapa("Mapa", Icons.Filled.Map),
    Rota("Rota", Icons.Filled.Navigation),
}

@Composable
fun StationBottomNavBar(
    selected: StationTab,
    onSelect: (StationTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StationTab.entries.forEach { tab ->
            val active = tab == selected
            val tint = if (active) PurpleBrand else TextFaint
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onSelect(tab) },
            ) {
                Icon(imageVector = tab.icon, contentDescription = tab.label, tint = tint)
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = tint,
                )
            }
        }
    }
}

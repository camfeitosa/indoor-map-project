package com.clickbus.challenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Check
import com.clickbus.challenge.model.StationPlace
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.InkDarker
import com.clickbus.challenge.ui.theme.Purple100
import com.clickbus.challenge.ui.theme.Purple200
import com.clickbus.challenge.ui.theme.Purple50
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextFaint

/** A single service/place row: icon, title, subtitle and a trailing chevron (or checkmark once selected). */
@Composable
fun StationPlaceRow(
    place: StationPlace,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (selected) Purple50 else Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, if (selected) Purple200 else BorderLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 17.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .size(40.dp)
                .background(place.iconBackground, RoundedCornerShape(14.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = place.icon,
                contentDescription = place.title,
                tint = place.iconTint,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = place.title, style = MaterialTheme.typography.bodyMedium, color = InkDarker)
            Text(text = place.subtitle, style = MaterialTheme.typography.bodySmall, color = TextFaint)
        }
        Row(
            modifier = Modifier
                .size(28.dp)
                .background(if (selected) PurpleBrand else Purple100, RoundedCornerShape(10.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (selected) Icons.Filled.Check else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (selected) Color.White else PurpleBrand,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

/** Compact circular chip used for boarding platform numbers ("12", "Plat."). */
@Composable
fun PlatformChip(
    number: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(17.dp))
            .border(1.dp, Purple100, RoundedCornerShape(17.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            modifier = Modifier
                .size(38.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(com.clickbus.challenge.ui.theme.PurpleBrand, com.clickbus.challenge.ui.theme.Purple400)
                    ),
                    CircleShape,
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = number, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = "Plat.", style = MaterialTheme.typography.bodySmall, color = InkDarker)
    }
}

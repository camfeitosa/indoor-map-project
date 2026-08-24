package com.clickbus.challenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.model.TripSummary
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.Purple100
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextFaint
import com.clickbus.challenge.ui.theme.TextSecondary

/** Card summarizing a booked trip: date, route, weekday/time and company/platform/seat. */
@Composable
fun TripCard(trip: TripSummary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier
                    .background(Purple100, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = trip.day, style = MaterialTheme.typography.titleSmall, color = PurpleBrand, fontWeight = FontWeight.Bold)
                Text(text = trip.month, style = MaterialTheme.typography.labelSmall, color = PurpleBrand)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${trip.originCity}, ${trip.originState}  →  ${trip.destinationCity}, ${trip.destinationState}",
                    style = MaterialTheme.typography.titleSmall,
                    color = InkDark,
                )
                Text(
                    text = "${trip.weekday} • ${trip.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Icon(Icons.Filled.DirectionsBus, contentDescription = null, tint = PurpleBrand, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TripDetail(label = "Empresa", value = trip.company, modifier = Modifier.weight(1.4f))
            TripDetail(label = "Plataforma", value = trip.platform, modifier = Modifier.weight(1f), highlighted = true)
            TripDetail(label = "Poltrona", value = trip.seat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TripDetail(label: String, value: String, modifier: Modifier = Modifier, highlighted: Boolean = false) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextFaint)
        Spacer(modifier = Modifier.height(4.dp))
        if (highlighted) {
            Box(
                modifier = Modifier
                    .background(Purple100, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = PurpleBrand, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = InkDark, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Pager dots indicating which trip is currently shown in the carousel. */
@Composable
fun CarouselDots(count: Int, activeIndex: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (index == activeIndex) 8.dp else 6.dp)
                    .background(if (index == activeIndex) PurpleBrand else BorderLight, CircleShape),
            )
        }
    }
}

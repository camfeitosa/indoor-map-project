package com.clickbus.challenge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.model.upcomingTrips
import com.clickbus.challenge.ui.components.AppBottomNavBar
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.components.CarouselDots
import com.clickbus.challenge.ui.components.PrimaryButton
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.components.TripCard
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.Purple400
import com.clickbus.challenge.ui.theme.Purple600
import com.clickbus.challenge.ui.theme.Purple700
import com.clickbus.challenge.ui.theme.SurfaceBgAlt
import com.clickbus.challenge.ui.theme.TextSecondary

/** "Minhas viagens" — just the upcoming-trips carousel plus a shortcut to locate the platform. */
@Composable
fun MyTripsScreen(
    onLocatePlatform: () -> Unit,
    onSelectTab: (AppTab) -> Unit = {},
) {
    StatusBarIcons(darkIcons = false)

    val pagerState = rememberPagerState(pageCount = { upcomingTrips.size })

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Purple700, Purple600, Purple400)))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            Text(text = "Minhas viagens", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text(
                text = "Acompanhe suas próximas viagens.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SurfaceBgAlt)
                .padding(20.dp),
        ) {
            if (upcomingTrips.isEmpty()) {
                Text(text = "Você ainda não tem viagens marcadas.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            } else {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                    TripCard(upcomingTrips[page])
                }
                Spacer(modifier = Modifier.height(12.dp))
                CarouselDots(count = upcomingTrips.size, activeIndex = pagerState.currentPage)

                Spacer(modifier = Modifier.height(28.dp))

                Text(text = "Precisa embarcar?", style = MaterialTheme.typography.titleSmall, color = InkDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Localize sua plataforma e trace a rota até ela.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryButton(
                    text = "Localizar Plataforma",
                    onClick = onLocatePlatform,
                )
            }
        }

        AppBottomNavBar(selected = AppTab.MinhasViagens, onSelect = onSelectTab)
    }
}

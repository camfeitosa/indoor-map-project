package com.clickbus.challenge.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.R
import com.clickbus.challenge.model.upcomingTrips
import com.clickbus.challenge.ui.components.AppBottomNavBar
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.components.CarouselDots
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.components.TripCard
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.InkDarker
import com.clickbus.challenge.ui.theme.Purple100
import com.clickbus.challenge.ui.theme.Purple400
import com.clickbus.challenge.ui.theme.Purple50
import com.clickbus.challenge.ui.theme.Purple600
import com.clickbus.challenge.ui.theme.Purple700
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextSecondary

private data class QuickAction(val label: String, val icon: ImageVector)

private val quickActions = listOf(
    QuickAction("Plataformas", Icons.Filled.DirectionsBus),
    QuickAction("Praça de\nalimentação", Icons.Filled.Restaurant),
    QuickAction("Banheiros", Icons.Filled.Wc),
    QuickAction("Bilheteria", Icons.Filled.ConfirmationNumber),
    QuickAction("Achados e\nperdidos", Icons.Filled.Luggage),
)

/** The mobile app's real home screen: greeting, quick actions, upcoming trip and promo banner. */
@Composable
fun AppHomeScreen(
    onFindPlatform: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenTrips: () -> Unit,
    onSelectTab: (AppTab) -> Unit = {},
) {
    StatusBarIcons(darkIcons = false)

    val density = LocalDensity.current
    var heroContentHeightPx by remember { mutableIntStateOf(with(density) { 280.dp.roundToPx() }) }
    val heroContentHeightDp = with(density) { heroContentHeightPx.toDp() }

    val pagerState = rememberPagerState(pageCount = { upcomingTrips.size })

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heroContentHeightDp)
                            .background(
                                Brush.linearGradient(listOf(Purple700, Purple600, Purple400)),
                                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                            ),
                    )

                    Column(
                        modifier = Modifier
                            .onGloballyPositioned { heroContentHeightPx = it.size.height }
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 20.dp, vertical = 26.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.clickbus_wordmark_white),
                                contentDescription = "ClickBus",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.height(74.dp),
                            )
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Filled.Notifications, contentDescription = "Notificações", tint = Color.White, modifier = Modifier.size(20.dp))
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 8.dp, end = 8.dp)
                                        .size(8.dp)
                                        .background(Color.White, CircleShape),
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = -GREETING_OVERLAP)
                                .padding(vertical = 12.dp, horizontal = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Olá, passageiro!", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pronto para a próxima viagem?",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.85f),
                                )
                            }
                            Image(
                                painter = painterResource(R.drawable.bus_illustration),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(width = 190.dp, height = 150.dp)
                                    .padding(start = 2.dp),
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(top = (heroContentHeightDp - CARD_OVERLAP).coerceAtLeast(0.dp))
                            .padding(horizontal = 20.dp),
                    ) {
                        LocatePlatformBanner(onFindPlatform)

                        Spacer(modifier = Modifier.height(28.dp))

                        SectionHeader(title = "Mais buscados na rodoviária", actionLabel = "Ver todos", onAction = onOpenSearch)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            quickActions.forEach { action ->
                                QuickActionItem(action = action, onClick = onOpenSearch)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        SectionHeader(title = "Suas próximas viagens", actionLabel = "Ver todas", onAction = onOpenTrips)
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                            TripCard(upcomingTrips[page])
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        CarouselDots(count = upcomingTrips.size, activeIndex = pagerState.currentPage)

                        Spacer(modifier = Modifier.height(24.dp))

                        Image(
                            painter = painterResource(R.drawable.promo_banner),
                            contentDescription = "Tudo para sua viagem em um só lugar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1657f / 949f)
                                .clip(RoundedCornerShape(20.dp)),
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        AppBottomNavBar(selected = AppTab.Inicio, onSelect = onSelectTab)
    }
}

/** How far the locate-platform card is pulled up into the purple hero banner. */
private val CARD_OVERLAP = 44.dp

/** How far the greeting row is pulled up — tuned to leave ~2dp gap below the logo, not overlap it. */
private val GREETING_OVERLAP = 6.dp

@Composable
private fun LocatePlatformBanner(onFindPlatform: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple50, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(PurpleBrand, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Precisando se localizar?", style = MaterialTheme.typography.titleSmall, color = InkDark)
            Text(
                text = "Encontre sua plataforma e navegue pela rodoviária.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .background(PurpleBrand, RoundedCornerShape(10.dp))
                    .clickable(onClick = onFindPlatform)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Encontrar plataforma", style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.SemiBold)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = InkDark)
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.bodySmall,
            color = PurpleBrand,
            modifier = Modifier.clickable(onClick = onAction),
        )
    }
}

@Composable
private fun QuickActionItem(action: QuickAction, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Purple100, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(action.icon, contentDescription = action.label, tint = PurpleBrand, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = action.label,
            style = MaterialTheme.typography.labelSmall,
            color = InkDarker,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

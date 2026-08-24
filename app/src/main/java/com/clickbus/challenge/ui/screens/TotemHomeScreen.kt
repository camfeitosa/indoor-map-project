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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clickbus.challenge.R
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.HomeSheetBg
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.Purple100
import com.clickbus.challenge.ui.theme.Purple200
import com.clickbus.challenge.ui.theme.Purple400
import com.clickbus.challenge.ui.theme.Purple50
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextSecondary

private val HeroHeadlineAccent = Color(0xFF8A1CF4)
private val HeroGradientDeep = Color(0xFF1E0748)
private val HeroGradientMid = Color(0xFF3B1381)
private val HeroGradientBright = Color(0xFF561FB8)

/** Totem's landing screen: branded hero, "explore the station" card and a connecting bottom sheet. */
@Composable
fun TotemHomeScreen(onExplore: () -> Unit) {
    StatusBarIcons(darkIcons = false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0.08f to HeroGradientDeep,
                        0.41f to HeroGradientMid,
                        0.56f to HeroGradientBright,
                        0.69f to Purple400,
                    ),
                ),
            ),
    ) {
        Image(
            painter = painterResource(R.drawable.home_watermark),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.66f)
                .height(140.dp)
                .alpha(0.1f),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.clickbus_wordmark_white),
                    contentDescription = "ClickBus",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(68.dp),
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Navegue pela\n")
                        withStyle(SpanStyle(color = HeroHeadlineAccent)) { append("rodoviária") }
                        append(" com facilidade")
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp, lineHeight = 34.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Encontre seu portão, serviços e facilidades de forma rápida e prática.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                ExploreStationCard(onClick = onExplore)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Image(
                    painter = painterResource(R.drawable.totem_map_illustration),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(463f / 341f),
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(HomeSheetBg, RoundedCornerShape(topStart = 60.dp))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = PurpleBrand, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = buildAnnotatedString {
                    append("Conectando você ao que importa, ")
                    withStyle(SpanStyle(color = PurpleBrand)) { append("onde você está.") }
                },
                style = MaterialTheme.typography.bodySmall,
                color = InkDark,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ExploreStationCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(listOf(Purple100, Purple50)), RoundedCornerShape(18.dp))
            .border(1.dp, Purple200, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(PurpleBrand, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Explorar a rodoviária",
                style = MaterialTheme.typography.titleSmall,
                color = PurpleBrand,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Ver o mapa e encontrar o que você precisa.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = PurpleBrand,
        )
    }
}

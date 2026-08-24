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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.R
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.HomeSheetBg
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.Purple400
import com.clickbus.challenge.ui.theme.Purple600
import com.clickbus.challenge.ui.theme.Purple700
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.TextSecondary

/** "Home ofc" — real entry point: purple hero with logo, bottom sheet choosing App vs Totem. */
@Composable
fun HomeScreen(
    onSelectTotem: () -> Unit,
    onSelectApp: () -> Unit,
) {
    StatusBarIcons(darkIcons = false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Purple700, Purple600, Purple400))),
    ) {
        // Decorative translucent circles, matching the design's floating orbs.
        Box(
            modifier = Modifier
                .offset(x = 180.dp, y = (-60).dp)
                .size(200.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 40.dp)
                .size(114.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape),
        )
        Image(
            painter = painterResource(R.drawable.home_watermark),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.66f)
                .height(170.dp)
                .alpha(0.1f),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.clickbus_wordmark_white),
                    contentDescription = "ClickBus",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(140.dp),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HomeSheetBg, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .padding(horizontal = 24.dp, vertical = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Como você deseja ")
                        withStyle(SpanStyle(color = PurpleBrand)) { append("iniciar?") }
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = InkDark,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Selecione o fluxo que deseja utilizar para iniciar sua jornada.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                StartOptionCard(
                    title = "App",
                    description = "Acessar o fluxo completo pelo aplicativo.",
                    iconRes = R.drawable.icon_phone_card,
                    onClick = onSelectApp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                StartOptionCard(
                    title = "Totem",
                    description = "Utilize o totem para iniciar o atendimento.",
                    iconRes = R.drawable.icon_totem_card,
                    onClick = onSelectTotem,
                )
            }
        }
    }
}

@Composable
private fun StartOptionCard(
    title: String,
    description: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = InkDark)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = PurpleBrand,
        )
    }
}

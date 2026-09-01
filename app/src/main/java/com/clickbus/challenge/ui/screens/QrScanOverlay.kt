package com.clickbus.challenge.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clickbus.challenge.R
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.AccentGreen
import com.clickbus.challenge.ui.theme.PurpleBrand
import kotlinx.coroutines.delay

private enum class ScanPhase { Scanning, Detected }

/**
 * Full-screen mock "camera" overlay simulating a QR read: a poster photo standing in for the
 * live camera feed, a viewfinder frame with an animated scan line, then a success state before
 * handing back to [onScanned].
 */
@Composable
fun QrScanOverlay(
    onScanned: () -> Unit,
    onClose: () -> Unit,
) {
    StatusBarIcons(darkIcons = false)

    var phase by remember { mutableStateOf(ScanPhase.Scanning) }

    LaunchedEffectScanSequence(
        onDetected = { phase = ScanPhase.Detected },
        onFinished = onScanned,
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            painter = painterResource(R.drawable.qr_scan_poster_mock),
            contentDescription = null,
            contentScale = ContentScale.None,
            modifier = Modifier.fillMaxSize(),
        )

        ScrimWithViewfinder(phase = phase)

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Escanear QR Code",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Fechar", tint = Color.White)
                }
            }

            Box(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedVisibility(visible = phase == ScanPhase.Scanning) {
                    Text(
                        text = "Aponte a câmera para o QR Code do totem",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
                AnimatedVisibility(visible = phase == ScanPhase.Detected) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = AccentGreen)
                        Text(
                            text = "  QR Code lido!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LaunchedEffectScanSequence(onDetected: () -> Unit, onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200L)
        onDetected()
        delay(900L)
        onFinished()
    }
}

@Composable
private fun ScrimWithViewfinder(phase: ScanPhase) {
    val infiniteTransition = rememberInfiniteTransition(label = "qr-scan-line")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "qr-scan-line-progress",
    )

    val frameColor by animateFloatAsState(
        targetValue = if (phase == ScanPhase.Detected) 1f else 0f,
        animationSpec = tween(300),
        label = "qr-frame-color",
    )
    val bracketColor = lerpColor(PurpleBrand, AccentGreen, frameColor)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
    ) {
        val frameSize = size.minDimension * 0.66f
        val frameTopLeft = Offset(
            x = (size.width - frameSize) / 2f,
            y = (size.height - frameSize) / 2f,
        )
        val cornerRadiusPx = 20.dp.toPx()

        drawRect(color = Color.Black.copy(alpha = 0.55f))
        drawRoundRect(
            color = Color.Transparent,
            topLeft = frameTopLeft,
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(cornerRadiusPx),
            blendMode = BlendMode.Clear,
        )

        val bracketLength = 26.dp.toPx()
        val strokeWidth = 4.dp.toPx()
        val inset = strokeWidth / 2f

        fun corner(origin: Offset, dx: Float, dy: Float) {
            drawLine(
                color = bracketColor,
                start = origin,
                end = Offset(origin.x + dx * bracketLength, origin.y),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = bracketColor,
                start = origin,
                end = Offset(origin.x, origin.y + dy * bracketLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }

        corner(Offset(frameTopLeft.x + inset, frameTopLeft.y + inset), 1f, 1f)
        corner(Offset(frameTopLeft.x + frameSize - inset, frameTopLeft.y + inset), -1f, 1f)
        corner(Offset(frameTopLeft.x + inset, frameTopLeft.y + frameSize - inset), 1f, -1f)
        corner(Offset(frameTopLeft.x + frameSize - inset, frameTopLeft.y + frameSize - inset), -1f, -1f)

        if (phase == ScanPhase.Scanning) {
            val lineY = frameTopLeft.y + frameSize * scanProgress
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, PurpleBrand.copy(alpha = 0.9f), Color.Transparent),
                    startY = lineY - 14.dp.toPx(),
                    endY = lineY + 14.dp.toPx(),
                ),
                topLeft = Offset(frameTopLeft.x, lineY - 14.dp.toPx()),
                size = Size(frameSize, 28.dp.toPx()),
            )
            drawLine(
                color = PurpleBrand,
                start = Offset(frameTopLeft.x, lineY),
                end = Offset(frameTopLeft.x + frameSize, lineY),
                strokeWidth = 2.dp.toPx(),
            )
        }
    }

    if (phase == ScanPhase.Detected) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(animationSpec = spring()) + fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AccentGreen),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

private fun lerpColor(start: Color, end: Color, fraction: Float): Color = Color(
    red = start.red + (end.red - start.red) * fraction,
    green = start.green + (end.green - start.green) * fraction,
    blue = start.blue + (end.blue - start.blue) * fraction,
    alpha = start.alpha + (end.alpha - start.alpha) * fraction,
)

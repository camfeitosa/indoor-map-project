package com.clickbus.challenge.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.clickbus.challenge.R
import com.clickbus.challenge.indoor.BeaconSimulator
import com.clickbus.challenge.indoor.DemoMapLocations
import com.clickbus.challenge.indoor.IndoorDirectionEngine
import com.clickbus.challenge.indoor.IndoorMapAssetLoader
import com.clickbus.challenge.indoor.IndoorRoute
import com.clickbus.challenge.indoor.IndoorRouteEngine
import com.clickbus.challenge.ui.components.AppBottomNavBar
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.components.StationTopBar
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.SurfaceBgAlt
import kotlin.math.max
import kotlin.math.min

@Composable
fun MapRouteScreen(
    originLabel: String?,
    destinationLabel: String,
    onBack: () -> Unit,
    onSelectTab: (AppTab) -> Unit,
) {
    StatusBarIcons(darkIcons = true)
    val context = LocalContext.current
    val mapData = remember { IndoorMapAssetLoader.loadTieteMap(context) }
    val originId = remember(originLabel) { DemoMapLocations.originId(originLabel) }
    val destinationId = remember(destinationLabel) { DemoMapLocations.destinationId(destinationLabel) }
    val initialRoute = remember(mapData, originId, destinationId) {
        IndoorRouteEngine.shortestPath(mapData, originId, destinationId)
    }
    val beaconSimulator = remember(initialRoute.points) { BeaconSimulator(initialRoute.points) }
    var currentPointId by remember(initialRoute.points) { mutableStateOf(originId) }
    var isSimulating by remember(initialRoute.points) { mutableStateOf(false) }
    val route = remember(mapData, currentPointId, destinationId) {
        IndoorRouteEngine.shortestPath(mapData, currentPointId, destinationId)
    }
    val directions = remember(route, destinationLabel) {
        IndoorDirectionEngine.directions(route, destinationLabel)
    }
    val currentPointName = mapData.pointsById[currentPointId]?.label ?: "Origem"
    val hasArrived = currentPointId == destinationId && route.isAvailable
    val startSimulation = {
        if (hasArrived) {
            beaconSimulator.reset()
            currentPointId = originId
        }
        isSimulating = true
    }
    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        if (result.values.all { it }) startSimulation()
    }

    LaunchedEffect(isSimulating, beaconSimulator) {
        if (!isSimulating) return@LaunchedEffect
        beaconSimulator.detections().collect { detection ->
            currentPointId = detection.pointId
            if (detection.pointId == destinationId) isSimulating = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        StationTopBar(
            title = "Rodoviária Tietê",
            subtitle = "$currentPointName → $destinationLabel",
            onBack = onBack,
        )
        Column(modifier = Modifier.weight(1f).background(SurfaceBgAlt)) {
            IndoorRouteMap(
                route = route,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
            RouteSummary(
                route = route,
                destinationLabel = destinationLabel,
                directions = directions.map { it.instruction },
                currentPointName = currentPointName,
                isSimulating = isSimulating,
                hasArrived = hasArrived,
                onSimulationAction = {
                    if (isSimulating) {
                        isSimulating = false
                    } else if (hasBluetoothPermission(context)) {
                        startSimulation()
                    } else {
                        bluetoothPermissionLauncher.launch(requiredBluetoothPermissions())
                    }
                },
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(14.dp))
        }
        AppBottomNavBar(selected = AppTab.Buscar, onSelect = onSelectTab)
    }
}

@Composable
private fun IndoorRouteMap(
    route: IndoorRoute,
    modifier: Modifier = Modifier,
) {
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    var mapSize by remember { mutableStateOf(IntSize.Zero) }
    var targetScale by remember { mutableFloatStateOf(1f) }
    var targetOffsetX by remember { mutableFloatStateOf(0f) }
    var targetOffsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(route.points, viewportSize, mapSize) {
        if (!route.isAvailable || viewportSize == IntSize.Zero || mapSize == IntSize.Zero) return@LaunchedEffect
        val minX = route.points.minOf { it.xPercent } / 100f
        val maxX = route.points.maxOf { it.xPercent } / 100f
        val minY = route.points.minOf { it.yPercent } / 100f
        val maxY = route.points.maxOf { it.yPercent } / 100f
        val routeWidth = max(0.12f, maxX - minX)
        val routeHeight = max(0.12f, maxY - minY)
        val viewportToMapHeight = viewportSize.height.toFloat() / mapSize.height
        val minimumScale = minimumCoverScale(viewportSize, mapSize)
        targetScale = min(4f, max(minimumScale, min(0.78f / routeWidth, viewportToMapHeight * 0.68f / routeHeight)))
        val centerX = (minX + maxX) / 2f
        val centerY = (minY + maxY) / 2f
        val desiredX = (0.5f - centerX) * mapSize.width * targetScale
        val desiredY = (0.5f - centerY) * mapSize.height * targetScale
        val bounded = boundedOffset(desiredX, desiredY, targetScale, viewportSize, mapSize)
        targetOffsetX = bounded.x
        targetOffsetY = bounded.y
    }

    val scale by animateFloatAsState(targetScale, tween(700), label = "routeZoom")
    val offsetX by animateFloatAsState(targetOffsetX, tween(700), label = "routePanX")
    val offsetY by animateFloatAsState(targetOffsetY, tween(700), label = "routePanY")

    Box(
        modifier = modifier
            .clipToBounds()
            .background(Color(0xFFE5E7EB))
            .onSizeChanged { viewportSize = it }
            .pointerInput(route.points) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val minimumScale = minimumCoverScale(viewportSize, mapSize)
                    val newScale = (targetScale * zoom).coerceIn(minimumScale, 4f)
                    val bounded = boundedOffset(
                        x = targetOffsetX + pan.x,
                        y = targetOffsetY + pan.y,
                        scale = newScale,
                        viewportSize = viewportSize,
                        mapSize = mapSize,
                    )
                    targetScale = newScale
                    targetOffsetX = bounded.x
                    targetOffsetY = bounded.y
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1427f / 761f)
                .onSizeChanged { mapSize = it }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                },
        ) {
            Image(
                painter = painterResource(R.drawable.tiete_terminal_map),
                contentDescription = "Planta do Terminal Rodoviário Tietê",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                route.points.zipWithNext().forEach { (from, to) ->
                    val start = Offset(size.width * from.xPercent / 100f, size.height * from.yPercent / 100f)
                    val end = Offset(size.width * to.xPercent / 100f, size.height * to.yPercent / 100f)
                    drawLine(Color.White.copy(alpha = 0.95f), start, end, 4.dp.toPx(), StrokeCap.Round)
                    drawLine(PurpleBrand, start, end, 2.dp.toPx(), StrokeCap.Round)
                }
                route.points.firstOrNull()?.let { point ->
                    val center = Offset(size.width * point.xPercent / 100f, size.height * point.yPercent / 100f)
                    drawCircle(Color.White, 5.dp.toPx(), center)
                    drawCircle(PurpleBrand, 3.dp.toPx(), center)
                }
                route.points.lastOrNull()?.let { point ->
                    val center = Offset(size.width * point.xPercent / 100f, size.height * point.yPercent / 100f)
                    drawCircle(Color.White, 5.dp.toPx(), center)
                    drawCircle(Color(0xFF16A34A), 3.dp.toPx(), center)
                }
            }
        }

        Text(
            text = "Use dois dedos para ampliar e arraste para explorar",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.62f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp),
        )
    }
}

@Composable
private fun RouteSummary(
    route: IndoorRoute,
    destinationLabel: String,
    directions: List<String>,
    currentPointName: String,
    isSimulating: Boolean,
    hasArrived: Boolean,
    onSimulationAction: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (route.isAvailable) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (hasArrived) "Destino alcançado" else "Beacon detectado",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleBrand,
                    )
                    Text(text = currentPointName, style = MaterialTheme.typography.bodySmall, color = InkDark)
                }
                Button(onClick = onSimulationAction) {
                    Text(
                        text = when {
                            hasArrived -> "Reiniciar"
                            isSimulating -> "Pausar"
                            else -> "Iniciar percurso"
                        },
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Filled.Navigation, contentDescription = null, tint = PurpleBrand, modifier = Modifier.size(18.dp))
                Text(text = "Passo a passo", style = MaterialTheme.typography.titleSmall, color = InkDark)
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 190.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(directions) { index, instruction ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .padding(11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                    ) {
                        Box(
                            modifier = Modifier.size(24.dp).background(PurpleBrand, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = (index + 1).toString(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                        Text(text = instruction, style = MaterialTheme.typography.bodySmall, color = InkDark)
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().background(PurpleBrand, RoundedCornerShape(12.dp)).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Filled.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                Text(
                    text = "Não encontramos uma rota conectada até $destinationLabel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
    }
}

private fun minimumCoverScale(viewportSize: IntSize, mapSize: IntSize): Float {
    if (viewportSize == IntSize.Zero || mapSize == IntSize.Zero) return 1f
    return max(
        viewportSize.width.toFloat() / mapSize.width,
        viewportSize.height.toFloat() / mapSize.height,
    ).coerceAtLeast(1f)
}

private fun boundedOffset(
    x: Float,
    y: Float,
    scale: Float,
    viewportSize: IntSize,
    mapSize: IntSize,
): Offset {
    if (viewportSize == IntSize.Zero || mapSize == IntSize.Zero) return Offset.Zero
    val maximumX = max(0f, (mapSize.width * scale - viewportSize.width) / 2f)
    val maximumY = max(0f, (mapSize.height * scale - viewportSize.height) / 2f)
    return Offset(x.coerceIn(-maximumX, maximumX), y.coerceIn(-maximumY, maximumY))
}

private fun requiredBluetoothPermissions(): Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
} else {
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
}

private fun hasBluetoothPermission(context: Context): Boolean = requiredBluetoothPermissions().all { permission ->
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

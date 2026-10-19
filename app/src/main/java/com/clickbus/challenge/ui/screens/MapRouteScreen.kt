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
import com.clickbus.challenge.data.route.RouteModule
import com.clickbus.challenge.domain.route.MapPosition
import com.clickbus.challenge.ui.components.AppBottomNavBar
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.components.StationTopBar
import com.clickbus.challenge.ui.components.StatusBarIcons
import com.clickbus.challenge.ui.theme.BorderLight
import com.clickbus.challenge.ui.theme.InkDark
import com.clickbus.challenge.ui.theme.PurpleBrand
import com.clickbus.challenge.ui.theme.SurfaceBgAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.clickbus.challenge.ui.screens.maproute.MapRouteUiState
import com.clickbus.challenge.ui.screens.maproute.MapRouteViewModel
import com.clickbus.challenge.ui.screens.maproute.messageRes
import kotlin.math.ceil
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
    val viewModel: MapRouteViewModel = viewModel(
        key = "map-route:$originLabel->$destinationLabel",
        factory = viewModelFactory {
            initializer { MapRouteViewModel(RouteModule.repository, originLabel, destinationLabel) }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        if (result.values.all { it }) viewModel.startSimulation()
    }
    val onSimulationAction = {
        val content = uiState as? MapRouteUiState.Content
        when {
            content == null -> Unit
            content.isSimulating -> viewModel.pauseSimulation()
            hasBluetoothPermission(context) -> viewModel.startSimulation()
            else -> bluetoothPermissionLauncher.launch(requiredBluetoothPermissions())
        }
    }

    // A tela de busca pode mandar a origem como texto vazio, não só null.
    val currentLabel = (uiState as? MapRouteUiState.Content)?.currentPlaceLabel
        ?: originLabel?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.map_route_origin_fallback)

    Column(modifier = Modifier.fillMaxSize()) {
        StationTopBar(
            title = stringResource(R.string.map_route_title),
            subtitle = stringResource(R.string.map_route_subtitle, currentLabel, destinationLabel),
            onBack = onBack,
        )
        Column(modifier = Modifier.weight(1f).background(SurfaceBgAlt)) {
            when (val state = uiState) {
                MapRouteUiState.Loading -> RouteLoading(modifier = Modifier.fillMaxWidth().weight(1f))
                is MapRouteUiState.Error -> RouteError(
                    messageRes = state.error.messageRes(),
                    onRetry = viewModel::load,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                )
                is MapRouteUiState.Content -> {
                    IndoorRouteMap(
                        path = state.route.path,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                    )
                    RouteSummary(state = state, onSimulationAction = onSimulationAction)
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(14.dp))
        }
        AppBottomNavBar(selected = AppTab.Buscar, onSelect = onSelectTab)
    }
}

@Composable
private fun RouteLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = PurpleBrand)
        Text(
            text = stringResource(R.string.map_route_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = InkDark,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

@Composable
private fun RouteError(
    @androidx.annotation.StringRes messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.bodyMedium,
            color = InkDark,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = stringResource(R.string.map_route_retry))
        }
    }
}

@Composable
private fun IndoorRouteMap(
    path: List<MapPosition>,
    modifier: Modifier = Modifier,
) {
    // Pontos da polyline da API, em percentual da planta (0..100), como o desenho já esperava.
    val points = remember(path) { path.map { Offset(it.x.toFloat(), it.y.toFloat()) } }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    var mapSize by remember { mutableStateOf(IntSize.Zero) }
    var targetScale by remember { mutableFloatStateOf(1f) }
    var targetOffsetX by remember { mutableFloatStateOf(0f) }
    var targetOffsetY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(points, viewportSize, mapSize) {
        if (points.isEmpty() || viewportSize == IntSize.Zero || mapSize == IntSize.Zero) return@LaunchedEffect
        val minX = points.minOf { it.x } / 100f
        val maxX = points.maxOf { it.x } / 100f
        val minY = points.minOf { it.y } / 100f
        val maxY = points.maxOf { it.y } / 100f
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
            .pointerInput(points) {
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
                contentDescription = stringResource(R.string.map_route_map_description),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                points.zipWithNext().forEach { (from, to) ->
                    val start = Offset(size.width * from.x / 100f, size.height * from.y / 100f)
                    val end = Offset(size.width * to.x / 100f, size.height * to.y / 100f)
                    drawLine(Color.White.copy(alpha = 0.95f), start, end, 4.dp.toPx(), StrokeCap.Round)
                    drawLine(PurpleBrand, start, end, 2.dp.toPx(), StrokeCap.Round)
                }
                points.firstOrNull()?.let { point ->
                    val center = Offset(size.width * point.x / 100f, size.height * point.y / 100f)
                    drawCircle(Color.White, 5.dp.toPx(), center)
                    drawCircle(PurpleBrand, 3.dp.toPx(), center)
                }
                points.lastOrNull()?.let { point ->
                    val center = Offset(size.width * point.x / 100f, size.height * point.y / 100f)
                    drawCircle(Color.White, 5.dp.toPx(), center)
                    drawCircle(Color(0xFF16A34A), 3.dp.toPx(), center)
                }
            }
        }

        Text(
            text = stringResource(R.string.map_route_gesture_hint),
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
    state: MapRouteUiState.Content,
    onSimulationAction: () -> Unit,
) {
    val route = state.route
    val minutes = max(1, ceil(route.estimatedSeconds / 60.0).toInt())
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        if (state.hasArrived) R.string.map_route_destination_reached else R.string.map_route_beacon_detected,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = PurpleBrand,
                )
                Text(text = state.currentPlaceLabel, style = MaterialTheme.typography.bodySmall, color = InkDark)
                Text(
                    text = stringResource(R.string.map_route_summary, route.distanceMeters.toInt(), minutes),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkDark,
                )
            }
            Button(onClick = onSimulationAction) {
                Text(
                    text = stringResource(
                        when {
                            state.hasArrived -> R.string.map_route_action_restart
                            state.isSimulating -> R.string.map_route_action_pause
                            else -> R.string.map_route_action_start
                        },
                    ),
                )
            }
        }
        state.recalculationError?.let {
            Text(
                text = stringResource(R.string.map_route_recalculation_failed),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Filled.Navigation, contentDescription = null, tint = PurpleBrand, modifier = Modifier.size(18.dp))
            Text(
                text = stringResource(R.string.map_route_step_by_step),
                style = MaterialTheme.typography.titleSmall,
                color = InkDark,
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(max = 190.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(route.steps) { index, step ->
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
                    // A instrução já vem pronta da API, em português.
                    Text(text = step.instruction, style = MaterialTheme.typography.bodySmall, color = InkDark)
                }
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

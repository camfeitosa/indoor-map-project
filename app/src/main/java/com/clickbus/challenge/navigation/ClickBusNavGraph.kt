package com.clickbus.challenge.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clickbus.challenge.model.upcomingTrips
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.screens.AppHomeScreen
import com.clickbus.challenge.ui.screens.MapLocationPickerScreen
import com.clickbus.challenge.ui.screens.MapRouteScreen
import com.clickbus.challenge.ui.screens.MyTripsScreen
import com.clickbus.challenge.ui.screens.SearchDestinationScreen

private object Routes {
    const val APP_HOME = "app_home"
    const val SEARCH = "search"
    const val MY_TRIPS = "my_trips"
    const val MAP_PICKER = "map_picker?preselected={preselected}"
    const val MAP_ROUTE = "map_route/{destination}?origin={origin}"

    fun mapPickerRoute(preselectedDestination: String? = null) =
        if (preselectedDestination != null) "map_picker?preselected=$preselectedDestination" else "map_picker"

    fun mapRoute(destination: String, origin: String? = null): String {
        val encodedDestination = Uri.encode(destination)
        val encodedOrigin = Uri.encode(origin.orEmpty())
        return "map_route/$encodedDestination?origin=$encodedOrigin"
    }
}

@Composable
fun ClickBusNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.APP_HOME) {
        composable(Routes.APP_HOME) {
            AppHomeScreen(
                onFindPlatform = { navController.navigate(Routes.mapPickerRoute()) },
                onOpenSearch = { navController.navigate(Routes.SEARCH) },
                onOpenTrips = { navController.navigate(Routes.MY_TRIPS) },
                onSelectTab = { tab -> handleAppTabNavigation(navController, tab) },
            )
        }

        composable(Routes.MY_TRIPS) {
            val myTripPlatform = upcomingTrips.firstOrNull()?.platform
            MyTripsScreen(
                onLocatePlatform = {
                    navController.navigate(Routes.mapPickerRoute(myTripPlatform?.let { "Plataforma $it" }))
                },
                onSelectTab = { tab -> handleAppTabNavigation(navController, tab) },
            )
        }

        composable(Routes.SEARCH) {
            SearchDestinationScreen(
                onBack = { navController.popBackStack() },
                onDestinationSelected = { place -> navController.navigate(Routes.mapRoute(place.title)) },
                onTraceRoute = { navController.navigate(Routes.mapRoute("Sua rota")) },
                onSelectTab = { tab -> handleAppTabNavigation(navController, tab) },
            )
        }

        composable(
            route = Routes.MAP_PICKER,
            arguments = listOf(navArgument("preselected") { nullable = true; defaultValue = null }),
        ) { backStackEntry ->
            val preselected = backStackEntry.arguments?.getString("preselected")
            MapLocationPickerScreen(
                preselectedDestination = preselected,
                onBack = { navController.popBackStack() },
                onGoToMap = { origin, destination -> navController.navigate(Routes.mapRoute(destination, origin)) },
                onSelectTab = { tab -> handleAppTabNavigation(navController, tab) },
            )
        }

        composable(
            route = Routes.MAP_ROUTE,
            arguments = listOf(
                navArgument("destination") { defaultValue = "Plataforma 12" },
                navArgument("origin") { nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val destination = backStackEntry.arguments?.getString("destination") ?: "Plataforma 12"
            val origin = backStackEntry.arguments?.getString("origin")
            MapRouteScreen(
                originLabel = origin,
                destinationLabel = destination,
                onBack = { navController.popBackStack() },
                onSelectTab = { tab -> handleAppTabNavigation(navController, tab) },
            )
        }
    }
}

private fun handleAppTabNavigation(navController: NavHostController, tab: AppTab) {
    when (tab) {
        AppTab.Inicio -> navController.navigate(Routes.APP_HOME) { popUpTo(Routes.APP_HOME) { inclusive = true } }
        AppTab.Buscar -> navController.navigate(Routes.SEARCH)
        AppTab.MinhasViagens -> navController.navigate(Routes.MY_TRIPS)
    }
}

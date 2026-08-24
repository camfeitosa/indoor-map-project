package com.clickbus.challenge.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.clickbus.challenge.model.upcomingTrips
import com.clickbus.challenge.ui.components.AppTab
import com.clickbus.challenge.ui.screens.AppHomeScreen
import com.clickbus.challenge.ui.screens.HomeScreen
import com.clickbus.challenge.ui.screens.MapLocationPickerScreen
import com.clickbus.challenge.ui.screens.MapRouteScreen
import com.clickbus.challenge.ui.screens.MyTripsScreen
import com.clickbus.challenge.ui.screens.SearchDestinationScreen
import com.clickbus.challenge.ui.screens.TotemHomeScreen
import com.clickbus.challenge.ui.screens.TotemMapScreen
import com.clickbus.challenge.ui.screens.TotemServicesScreen

private object Routes {
    const val HOME = "home"
    const val APP_HOME = "app_home"
    const val TOTEM_HOME = "totem_home"
    const val SEARCH = "search"
    const val MY_TRIPS = "my_trips"
    const val MAP_PICKER = "map_picker?preselected={preselected}"
    const val MAP_ROUTE = "map_route/{destination}"
    const val TOTEM_SERVICES = "totem_services"
    const val TOTEM_MAP = "totem_map"

    fun mapPickerRoute(preselectedDestination: String? = null) =
        if (preselectedDestination != null) "map_picker?preselected=$preselectedDestination" else "map_picker"

    fun mapRoute(destination: String) = "map_route/$destination"
}

@Composable
fun ClickBusNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onSelectTotem = { navController.navigate(Routes.TOTEM_HOME) },
                onSelectApp = { navController.navigate(Routes.APP_HOME) },
            )
        }

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

        composable(Routes.TOTEM_HOME) {
            TotemHomeScreen(
                onExplore = { navController.navigate(Routes.TOTEM_SERVICES) },
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
                onGoToMap = { destination -> navController.navigate(Routes.mapRoute(destination)) },
                onSelectTab = { tab -> handleAppTabNavigation(navController, tab) },
            )
        }

        composable(
            route = Routes.MAP_ROUTE,
            arguments = listOf(navArgument("destination") { defaultValue = "Plataforma 12" }),
        ) { backStackEntry ->
            val destination = backStackEntry.arguments?.getString("destination") ?: "Plataforma 12"
            MapRouteScreen(
                destinationLabel = "Táxi → $destination",
                onBack = { navController.popBackStack() },
                onSelectTab = { tab -> handleAppTabNavigation(navController, tab) },
            )
        }

        composable(Routes.TOTEM_SERVICES) {
            TotemServicesScreen(
                onBack = { navController.popBackStack() },
                onSelectPlatform = { _, _ -> navController.navigate(Routes.TOTEM_MAP) },
            )
        }

        composable(Routes.TOTEM_MAP) {
            TotemMapScreen(
                onBack = { navController.popBackStack() },
                onRouteSentToPhone = { navController.navigate(Routes.APP_HOME) { popUpTo(Routes.HOME) } },
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

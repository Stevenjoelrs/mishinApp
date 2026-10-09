package com.mishin.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mishin.app.navigation.TopLevelDestination
import com.mishin.feature.menu.MenuScreen

/**
 * Main navigation host wiring all top-level destinations.
 * Menu has a real screen; the rest render a shared placeholder
 * until their feature phases are implemented.
 */
@Composable
fun MishinNavHost(
    navController: NavHostController,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.Dashboard.route,
        modifier = modifier
    ) {
        placeholder(TopLevelDestination.Dashboard, onOpenDrawer)
        placeholder(TopLevelDestination.Orders, onOpenDrawer)

        composable(TopLevelDestination.Menu.route) {
            MenuScreen(onOpenDrawer = onOpenDrawer)
        }

        placeholder(TopLevelDestination.Inventory, onOpenDrawer)
        placeholder(TopLevelDestination.Reservations, onOpenDrawer)
        placeholder(TopLevelDestination.Games, onOpenDrawer)
        placeholder(TopLevelDestination.Cats, onOpenDrawer)
        placeholder(TopLevelDestination.Reports, onOpenDrawer)
        placeholder(TopLevelDestination.Settings, onOpenDrawer)
    }
}

private fun NavGraphBuilder.placeholder(
    destination: TopLevelDestination,
    onOpenDrawer: () -> Unit
) {
    composable(destination.route) {
        PlaceholderScreen(
            title = destination.title,
            icon = destination.icon,
            onOpenDrawer = onOpenDrawer
        )
    }
}

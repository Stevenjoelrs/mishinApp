package com.mishin.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mishin.app.navigation.TopLevelDestination

/**
 * Main navigation host that will wire all feature screens.
 * Each feature module exposes its own navigation graph extension.
 *
 * For now, this is a skeleton — each feature will register its
 * composable destinations as they are implemented.
 */
@Composable
fun MishinNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.Dashboard.route
    ) {
        // Phase 1: Dashboard
        composable(TopLevelDestination.Dashboard.route) {
            // DashboardScreen(navController)
        }

        // Phase 3: Orders
        composable(TopLevelDestination.Orders.route) {
            // OrdersScreen(navController)
        }

        // Phase 3: Menu
        composable(TopLevelDestination.Menu.route) {
            // MenuScreen(navController)
        }

        // Phase 2: Inventory
        composable(TopLevelDestination.Inventory.route) {
            // InventoryScreen(navController)
        }

        // Phase 5: Reservations
        composable(TopLevelDestination.Reservations.route) {
            // ReservationsScreen(navController)
        }

        // Phase 6: Games
        composable(TopLevelDestination.Games.route) {
            // GamesScreen(navController)
        }

        // Phase 6: Cats
        composable(TopLevelDestination.Cats.route) {
            // CatsScreen(navController)
        }

        // Phase 8: Reports
        composable(TopLevelDestination.Reports.route) {
            // ReportsScreen(navController)
        }

        // Phase 8: Settings
        composable(TopLevelDestination.Settings.route) {
            // SettingsScreen(navController)
        }
    }
}

package com.mishin.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Top-level navigation destinations for the app.
 * Each maps to a feature module's entry screen.
 */
sealed class TopLevelDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : TopLevelDestination(
        route = "dashboard",
        title = "Inicio",
        icon = Icons.Default.Dashboard
    )

    data object Orders : TopLevelDestination(
        route = "orders",
        title = "Pedidos",
        icon = Icons.Default.Receipt
    )

    data object Menu : TopLevelDestination(
        route = "menu",
        title = "Menú",
        icon = Icons.Default.Restaurant
    )

    data object Inventory : TopLevelDestination(
        route = "inventory",
        title = "Inventario",
        icon = Icons.Default.Inventory2
    )

    data object Reservations : TopLevelDestination(
        route = "reservations",
        title = "Reservas",
        icon = Icons.Default.CalendarMonth
    )

    data object Games : TopLevelDestination(
        route = "games",
        title = "Juegos",
        icon = Icons.Default.Casino
    )

    data object Cats : TopLevelDestination(
        route = "cats",
        title = "Gatos",
        icon = Icons.Default.Pets
    )

    data object Reports : TopLevelDestination(
        route = "reports",
        title = "Reportes",
        icon = Icons.Default.TrendingUp
    )

    data object Settings : TopLevelDestination(
        route = "settings",
        title = "Ajustes",
        icon = Icons.Default.Settings
    )

    companion object {
        /**
         * Destinations shown in the navigation drawer / bottom bar.
         */
        val drawerItems = listOf(
            Dashboard, Orders, Menu, Inventory,
            Reservations, Games, Cats, Reports, Settings
        )
    }
}

package com.mishin.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Settings
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
        icon = Icons.Outlined.Home
    )

    data object Orders : TopLevelDestination(
        route = "orders",
        title = "Pedidos",
        icon = Icons.Outlined.Receipt
    )

    data object Menu : TopLevelDestination(
        route = "menu",
        title = "Menú",
        icon = Icons.Outlined.Restaurant
    )

    data object Inventory : TopLevelDestination(
        route = "inventory",
        title = "Inventario",
        icon = Icons.Outlined.Inventory2
    )

    data object Reservations : TopLevelDestination(
        route = "reservations",
        title = "Reservas",
        icon = Icons.Outlined.CalendarMonth
    )

    data object Games : TopLevelDestination(
        route = "games",
        title = "Juegos",
        icon = Icons.Outlined.Casino
    )

    data object Cats : TopLevelDestination(
        route = "cats",
        title = "Gatos",
        icon = Icons.Outlined.Pets
    )

    data object Reports : TopLevelDestination(
        route = "reports",
        title = "Reportes",
        icon = Icons.AutoMirrored.Outlined.TrendingUp
    )

    data object Settings : TopLevelDestination(
        route = "settings",
        title = "Ajustes",
        icon = Icons.Outlined.Settings
    )

    companion object {
        /**
         * Destinations shown in the navigation drawer / bottom bar.
         * Computed on access: building it inside the companion initializer
         * can capture nulls when a nested object initializes the sealed
         * class first (class-init order on the JVM/D8).
         */
        val drawerItems: List<TopLevelDestination>
            get() = listOf(
                Dashboard, Orders, Menu, Inventory,
                Reservations, Games, Cats, Reports, Settings
            )
    }
}

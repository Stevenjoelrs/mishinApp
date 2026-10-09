package com.mishin.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mishin.app.navigation.MishinDrawer
import com.mishin.core.ui.theme.MishinTheme
import kotlinx.coroutines.launch

/**
 * Root composable that applies the Mishin theme and hosts the
 * navigation drawer plus the main navigation host.
 */
@Composable
fun MishinApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    MishinTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen,
                drawerContent = {
                    MishinDrawer(
                        currentRoute = currentRoute,
                        onDestinationClick = { destination ->
                            scope.launch { drawerState.close() }
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onClose = { scope.launch { drawerState.close() } },
                        onLogout = { scope.launch { drawerState.close() } }
                    )
                }
            ) {
                MishinNavHost(
                    navController = navController,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}

package com.popcinefr.popcinefrapp.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar {
        // currentBackStackEntry tells us which screen is currently showing
        // We use it to highlight the correct bottom bar icon
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        // This prevents stacking the same screen multiple times
                        // when tapping the same bottom bar icon repeatedly
                        launchSingleTop = true
                        // Go back to home when switching tabs
                        // instead of building a huge back stack
                        restoreState = true
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
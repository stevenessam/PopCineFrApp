package com.popcinefr.popcinefrapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

// sealed class means only these screens exist — no surprises
sealed class Screen(val route: String) {

    // Bottom bar screens
    object Home : Screen("home")
    object Search : Screen("search")
    object Favorites : Screen("favorites")

    // Detail screens — notice the {id} in the route
    // This is how we pass the movie/series ID to the detail screen
    object MovieDetail : Screen("movie_detail/{id}") {
        // Helper function to build the route with a real ID
        // e.g. createRoute(123) → "movie_detail/123"
        fun createRoute(id: Int) = "movie_detail/$id"
    }

    object SeriesDetail : Screen("series_detail/{id}") {
        fun createRoute(id: Int) = "series_detail/$id"
    }
}

// This data class represents a bottom bar item
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

// The 3 items in our bottom bar
val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Search, "Search", Icons.Filled.Search),
    BottomNavItem(Screen.Favorites, "Favorites", Icons.Filled.Favorite)
)
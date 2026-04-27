package com.popcinefr.popcinefrapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {

    object Home : Screen("home")
    object Search : Screen("search")
    object Favorites : Screen("favorites")

    object MovieDetail : Screen("movie_detail/{id}") {
        fun createRoute(id: Int) = "movie_detail/$id"
    }

    object SeriesDetail : Screen("series_detail/{id}") {
        fun createRoute(id: Int) = "series_detail/$id"
    }

    object SeeAllMovies : Screen("see_all_movies/{category}") {
        fun createRoute(category: String) = "see_all_movies/$category"
    }

    object SeeAllSeries : Screen("see_all_series/{category}") {
        fun createRoute(category: String) = "see_all_series/$category"
    }

    object SeeAllMoviesByGenre : Screen("see_all_movies_genre/{genreId}/{genreName}") {
        fun createRoute(genreId: Int, genreName: String) =
            "see_all_movies_genre/$genreId/$genreName"
    }

    object SeeAllSeriesByGenre : Screen("see_all_series_genre/{genreId}/{genreName}") {
        fun createRoute(genreId: Int, genreName: String) =
            "see_all_series_genre/$genreId/$genreName"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Search, "Search", Icons.Filled.Search),
    BottomNavItem(Screen.Favorites, "Favorites", Icons.Filled.Favorite)
)
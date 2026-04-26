package com.popcinefr.popcinefrapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.popcinefr.popcinefrapp.presentation.detail.MovieDetailScreen
import com.popcinefr.popcinefrapp.presentation.detail.SeriesDetailScreen
import com.popcinefr.popcinefrapp.presentation.favorites.FavoritesScreen
import com.popcinefr.popcinefrapp.presentation.home.HomeScreen
import com.popcinefr.popcinefrapp.presentation.search.SearchScreen

@Composable
fun NavGraph() {
    // NavController is the object that controls navigation
    // rememberNavController keeps it alive across recompositions
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // We only show the bottom bar on the 3 main screens
            // Not on detail screens
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->

        // NavHost is the container that shows the current screen
        // startDestination is the first screen shown when app opens
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { id ->
                        // Navigate to movie detail passing the ID
                        navController.navigate(Screen.MovieDetail.createRoute(id))
                    },
                    onSeriesClick = { id ->
                        navController.navigate(Screen.SeriesDetail.createRoute(id))
                    }
                )
            }

            // Search Screen
            composable(Screen.Search.route) {
                SearchScreen(
                    onMovieClick = { id ->
                        navController.navigate(Screen.MovieDetail.createRoute(id))
                    },
                    onSeriesClick = { id ->
                        navController.navigate(Screen.SeriesDetail.createRoute(id))
                    }
                )
            }

            // Favorites Screen
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onMovieClick = { id ->
                        navController.navigate(Screen.MovieDetail.createRoute(id))
                    },
                    onSeriesClick = { id ->
                        navController.navigate(Screen.SeriesDetail.createRoute(id))
                    }
                )
            }

            // Movie Detail Screen
            // We declare the argument type so Navigation knows it's an Int
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                // Extract the ID from the route
                val movieId = backStackEntry.arguments?.getInt("id") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Series Detail Screen
            composable(
                route = Screen.SeriesDetail.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getInt("id") ?: return@composable
                SeriesDetailScreen(
                    seriesId = seriesId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
package com.popcinefr.popcinefrapp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.popcinefr.popcinefrapp.presentation.detail.MovieDetailScreen
import com.popcinefr.popcinefrapp.presentation.detail.SeriesDetailScreen
import com.popcinefr.popcinefrapp.presentation.favorites.FavoritesScreen
import com.popcinefr.popcinefrapp.presentation.home.HomeScreen
import com.popcinefr.popcinefrapp.presentation.home.HomeViewModel
import com.popcinefr.popcinefrapp.presentation.search.SearchScreen
import com.popcinefr.popcinefrapp.presentation.seeall.SeeAllScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { id ->
                        navController.navigate(Screen.MovieDetail.createRoute(id))
                    },
                    onSeriesClick = { id ->
                        navController.navigate(Screen.SeriesDetail.createRoute(id))
                    },
                    onSeeAllMovies = { category ->
                        navController.navigate(Screen.SeeAllMovies.createRoute(category))
                    },
                    onSeeAllSeries = { category ->
                        navController.navigate(Screen.SeeAllSeries.createRoute(category))
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
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
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

            // See All Movies Screen
            composable(
                route = Screen.SeeAllMovies.route,
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: return@composable
                val viewModel: HomeViewModel = viewModel()
                val seeAllState by viewModel.seeAllMovies.collectAsState()

                LaunchedEffect(category) {
                    viewModel.loadSeeAllMovies(category)
                }

                SeeAllScreen(
                    title = when (category) {
                        "trending" -> "🔥 Trending Movies"
                        "top_rated" -> "🏆 Top Rated Movies"
                        "now_playing" -> "🎬 Now Playing"
                        else -> "Movies"
                    },
                    uiState = seeAllState,
                    itemKey = { it.id },
                    itemTitle = { it.title },
                    itemPoster = { it.posterPath },
                    itemRating = { it.voteAverage },
                    onItemClick = { movie ->
                        navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // See All Series Screen
            composable(
                route = Screen.SeeAllSeries.route,
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: return@composable
                val viewModel: HomeViewModel = viewModel()
                val seeAllState by viewModel.seeAllSeries.collectAsState()

                LaunchedEffect(category) {
                    viewModel.loadSeeAllSeries(category)
                }

                SeeAllScreen(
                    title = when (category) {
                        "trending" -> "🔥 Trending Series"
                        "top_rated" -> "🏆 Top Rated Series"
                        "on_the_air" -> "📡 On The Air"
                        else -> "Series"
                    },
                    uiState = seeAllState,
                    itemKey = { it.id },
                    itemTitle = { it.name },
                    itemPoster = { it.posterPath },
                    itemRating = { it.voteAverage },
                    onItemClick = { series ->
                        navController.navigate(Screen.SeriesDetail.createRoute(series.id))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
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

            // Home
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
                    },
                    onSeeAllMoviesByGenre = { genreId, genreName ->
                        navController.navigate(
                            Screen.SeeAllMoviesByGenre.createRoute(genreId, genreName)
                        )
                    },
                    onSeeAllSeriesByGenre = { genreId, genreName ->
                        navController.navigate(
                            Screen.SeeAllSeriesByGenre.createRoute(genreId, genreName)
                        )
                    }
                )
            }

            // Search
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

            // Favorites
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

            // Movie Detail
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("id") ?: return@composable
                MovieDetailScreen(
                    movieId = movieId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Series Detail
            composable(
                route = Screen.SeriesDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getInt("id") ?: return@composable
                SeriesDetailScreen(
                    seriesId = seriesId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // See All Movies
            composable(
                route = Screen.SeeAllMovies.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: return@composable
                val viewModel: HomeViewModel = viewModel()
                val seeAllState by viewModel.seeAllMovies.collectAsState()

                LaunchedEffect(category) { viewModel.loadSeeAllMovies(category) }

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
                    onItemClick = { navController.navigate(Screen.MovieDetail.createRoute(it.id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // See All Series
            composable(
                route = Screen.SeeAllSeries.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: return@composable
                val viewModel: HomeViewModel = viewModel()
                val seeAllState by viewModel.seeAllSeries.collectAsState()

                LaunchedEffect(category) { viewModel.loadSeeAllSeries(category) }

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
                    onItemClick = { navController.navigate(Screen.SeriesDetail.createRoute(it.id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // See All Movies by Genre
            composable(
                route = Screen.SeeAllMoviesByGenre.route,
                arguments = listOf(
                    navArgument("genreId") { type = NavType.IntType },
                    navArgument("genreName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val genreId = backStackEntry.arguments?.getInt("genreId") ?: return@composable
                val genreName = backStackEntry.arguments?.getString("genreName") ?: return@composable
                val viewModel: HomeViewModel = viewModel()
                val seeAllState by viewModel.seeAllMoviesByGenre.collectAsState()

                LaunchedEffect(genreId) { viewModel.loadSeeAllMoviesByGenre(genreId) }

                SeeAllScreen(
                    title = "🎭 $genreName Movies",
                    uiState = seeAllState,
                    itemKey = { it.id },
                    itemTitle = { it.title },
                    itemPoster = { it.posterPath },
                    itemRating = { it.voteAverage },
                    onItemClick = { navController.navigate(Screen.MovieDetail.createRoute(it.id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // See All Series by Genre
            composable(
                route = Screen.SeeAllSeriesByGenre.route,
                arguments = listOf(
                    navArgument("genreId") { type = NavType.IntType },
                    navArgument("genreName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val genreId = backStackEntry.arguments?.getInt("genreId") ?: return@composable
                val genreName = backStackEntry.arguments?.getString("genreName") ?: return@composable
                val viewModel: HomeViewModel = viewModel()
                val seeAllState by viewModel.seeAllSeriesByGenre.collectAsState()

                LaunchedEffect(genreId) { viewModel.loadSeeAllSeriesByGenre(genreId) }

                SeeAllScreen(
                    title = "🎭 $genreName Series",
                    uiState = seeAllState,
                    itemKey = { it.id },
                    itemTitle = { it.name },
                    itemPoster = { it.posterPath },
                    itemRating = { it.voteAverage },
                    onItemClick = { navController.navigate(Screen.SeriesDetail.createRoute(it.id)) },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
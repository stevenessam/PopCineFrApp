package com.popcinefr.popcinefrapp.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.util.UiState

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBackClick: () -> Unit
) {
    val viewModel: DetailViewModel = viewModel()
    val movieDetailState by viewModel.movieDetail.collectAsState()

    // Load the movie when this screen first appears
    // LaunchedEffect runs once when movieId changes
    LaunchedEffect(movieId) {
        viewModel.loadMovieDetail(movieId)
    }

    when (val state = movieDetailState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }

        is UiState.Success -> {
            val movie = state.data
            DetailContent(
                title = movie.title,
                overview = movie.overview,
                posterPath = movie.posterPath,
                backdropPath = movie.backdropPath,
                voteAverage = movie.voteAverage,
                releaseDate = movie.releaseDate,
                // Format runtime as "🕐 148 min"
                extraInfo = movie.runtime?.let { "🕐 $it min" },
                genres = movie.genres,
                videos = movie.videos.results,
                isFavorite = false,         // we'll wire this in Step 7
                onFavoriteClick = { },      // we'll wire this in Step 7
                onBackClick = onBackClick
            )
        }
    }
}
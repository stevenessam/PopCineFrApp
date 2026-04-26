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
fun SeriesDetailScreen(
    seriesId: Int,
    onBackClick: () -> Unit
) {
    val viewModel: DetailViewModel = viewModel()
    val seriesDetailState by viewModel.seriesDetail.collectAsState()

    LaunchedEffect(seriesId) {
        viewModel.loadSeriesDetail(seriesId)
    }

    when (val state = seriesDetailState) {
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
            val series = state.data
            DetailContent(
                title = series.name,
                overview = series.overview,
                posterPath = series.posterPath,
                backdropPath = series.backdropPath,
                voteAverage = series.voteAverage,
                releaseDate = series.firstAirDate,
                // Show seasons and episodes info instead of runtime
                extraInfo = series.numberOfSeasons?.let {
                    "📺 $it seasons · ${series.numberOfEpisodes} episodes"
                },
                genres = series.genres,
                videos = series.videos.results,
                isFavorite = false,         // we'll wire this in Step 7
                onFavoriteClick = { },      // we'll wire this in Step 7
                onBackClick = onBackClick
            )
        }
    }
}
package com.popcinefr.popcinefrapp.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.popcinefr.popcinefrapp.R
import com.popcinefr.popcinefrapp.presentation.components.MediaCard
import com.popcinefr.popcinefrapp.presentation.home.HomeViewModel
import com.popcinefr.popcinefrapp.util.UiState
import com.popcinefr.popcinefrapp.util.seriesGenres
import com.popcinefr.popcinefrapp.presentation.components.SeeAllButton

@Composable
fun SeriesContent(
    viewModel: HomeViewModel,
    onSeriesClick: (Int) -> Unit,
    onSeeAllSeries: (String) -> Unit,
    onSeeAllSeriesByGenre: (Int, String) -> Unit
) {
    val onTheAirSeries by viewModel.onTheAirSeries.collectAsState()
    val mostWatchedSeries by viewModel.mostWatchedSeries.collectAsState()
    val seriesByGenre by viewModel.seriesByGenre.collectAsState()
    val selectedGenre by viewModel.selectedSeriesGenre.collectAsState()

    SectionWithIcon(
        title = "On The Air",
        icon = Icons.Filled.Tv,
        onSeeAllClick = { onSeeAllSeries("on_the_air") }
    )
    when (onTheAirSeries) {
        is UiState.Loading -> LoadingRow()
        is UiState.Error -> ErrorRow((onTheAirSeries as UiState.Error).message)
        is UiState.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = (onTheAirSeries as UiState.Success).data,
                    key = { it.id }
                ) { series ->
                    MediaCard(
                        title = series.name,
                        posterPath = series.posterPath,
                        rating = series.voteAverage,
                        onClick = { onSeriesClick(series.id) }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    SectionWithIcon(
        title = "Most Watched",
        icon = Icons.Filled.Visibility,
        onSeeAllClick = { onSeeAllSeries("most_watched") }
    )

    when (mostWatchedSeries) {
        is UiState.Loading -> LoadingRow()
        is UiState.Error -> ErrorRow((mostWatchedSeries as UiState.Error).message)
        is UiState.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = (mostWatchedSeries as UiState.Success).data,
                    key = { it.id }
                ) { series ->
                    MediaCard(
                        title = series.name,
                        posterPath = series.posterPath,
                        rating = series.voteAverage,
                        onClick = { onSeriesClick(series.id) }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    GenreDropdownSection(
        genres = seriesGenres,
        selectedGenre = selectedGenre,
        onGenreSelected = { viewModel.loadSeriesByGenre(it) },
        onSeeAllClick = { onSeeAllSeriesByGenre(selectedGenre.id, selectedGenre.name) }
    )

    GenreMediaCarousel(
        uiState = seriesByGenre,
        itemKey = { it.id },
        itemTitle = { it.name },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onSeriesClick(it.id) }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

package com.popcinefr.popcinefrapp.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
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
import com.popcinefr.popcinefrapp.util.movieGenres
import com.popcinefr.popcinefrapp.presentation.components.SeeAllButton

@Composable
fun MoviesContent(
    viewModel: HomeViewModel,
    onMovieClick: (Int) -> Unit,
    onSeeAllMovies: (String) -> Unit,
    onSeeAllMoviesByGenre: (Int, String) -> Unit
) {
    val nowPlayingMovies by viewModel.nowPlayingMovies.collectAsState()
    val mostWatchedMovies by viewModel.mostWatchedMovies.collectAsState()
    val moviesByGenre by viewModel.moviesByGenre.collectAsState()
    val selectedGenre by viewModel.selectedMovieGenre.collectAsState()

    // Now Playing
    SectionWithIcon(
        title = stringResource(R.string.movies),
        subtitle = "Now Playing", // Or fresh release
        icon = Icons.Filled.Movie,
        onSeeAllClick = { onSeeAllMovies("now_playing") }
    )
    when (nowPlayingMovies) {
        is UiState.Loading -> LoadingRow()
        is UiState.Error -> ErrorRow((nowPlayingMovies as UiState.Error).message)
        is UiState.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = (nowPlayingMovies as UiState.Success).data,
                    key = { it.id }
                ) { movie ->
                    MediaCard(
                        title = movie.title,
                        posterPath = movie.posterPath,
                        rating = movie.voteAverage,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Most Watched
    SectionWithIcon(
        title = "Most Watched",
        icon = Icons.Filled.Visibility,
        onSeeAllClick = { onSeeAllMovies("most_watched") }
    )

    when (mostWatchedMovies) {
        is UiState.Loading -> LoadingRow()
        is UiState.Error -> ErrorRow((mostWatchedMovies as UiState.Error).message)
        is UiState.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = (mostWatchedMovies as UiState.Success).data,
                    key = { it.id }
                ) { movie ->
                    MediaCard(
                        title = movie.title,
                        posterPath = movie.posterPath,
                        rating = movie.voteAverage,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))

    // Genre section
    GenreDropdownSection(
        genres = movieGenres,
        selectedGenre = selectedGenre,
        onGenreSelected = { viewModel.loadMoviesByGenre(it) },
        onSeeAllClick = { onSeeAllMoviesByGenre(selectedGenre.id, selectedGenre.name) }
    )

    GenreMediaCarousel(
        uiState = moviesByGenre,
        itemKey = { it.id },
        itemTitle = { it.title },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onMovieClick(it.id) }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

package com.popcinefr.popcinefrapp.presentation.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.presentation.components.MediaSection
import com.popcinefr.popcinefrapp.util.Genre
import com.popcinefr.popcinefrapp.util.movieGenres
import com.popcinefr.popcinefrapp.util.seriesGenres

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit,
    onSeeAllMovies: (String) -> Unit,
    onSeeAllSeries: (String) -> Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("PopCineFR 🎬", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- Tab Toggle Buttons ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val moviesColor by animateColorAsState(
                    targetValue = if (selectedTab == HomeTab.MOVIES)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    label = "moviesColor"
                )

                Button(
                    onClick = { viewModel.onTabSelected(HomeTab.MOVIES) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = moviesColor
                    )
                ) {
                    Text(
                        text = "🎬 Movies",
                        color = if (selectedTab == HomeTab.MOVIES)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val seriesColor by animateColorAsState(
                    targetValue = if (selectedTab == HomeTab.SERIES)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    label = "seriesColor"
                )

                Button(
                    onClick = { viewModel.onTabSelected(HomeTab.SERIES) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = seriesColor
                    )
                ) {
                    Text(
                        text = "📺 Series",
                        color = if (selectedTab == HomeTab.SERIES)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- Content based on selected tab ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (selectedTab == HomeTab.MOVIES) {
                    MoviesContent(
                        viewModel = viewModel,
                        onMovieClick = onMovieClick,
                        onSeeAllMovies = onSeeAllMovies
                    )
                } else {
                    SeriesContent(
                        viewModel = viewModel,
                        onSeriesClick = onSeriesClick,
                        onSeeAllSeries = onSeeAllSeries
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesContent(
    viewModel: HomeViewModel,
    onMovieClick: (Int) -> Unit,
    onSeeAllMovies: (String) -> Unit
) {
    val trendingMovies by viewModel.trendingMovies.collectAsState()
    val topRatedMovies by viewModel.topRatedMovies.collectAsState()
    val nowPlayingMovies by viewModel.nowPlayingMovies.collectAsState()
    val moviesByGenre by viewModel.moviesByGenre.collectAsState()
    val selectedGenre by viewModel.selectedMovieGenre.collectAsState()

    Spacer(modifier = Modifier.height(8.dp))

    MediaSection(
        title = "🔥 Trending",
        uiState = trendingMovies,
        itemKey = { it.id },
        itemTitle = { it.title },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onMovieClick(it.id) },
        onSeeAllClick = { onSeeAllMovies("trending") }
    )

    MediaSection(
        title = "🏆 Top Rated",
        uiState = topRatedMovies,
        itemKey = { it.id },
        itemTitle = { it.title },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onMovieClick(it.id) },
        onSeeAllClick = { onSeeAllMovies("top_rated") }
    )

    MediaSection(
        title = "🎬 Now Playing",
        uiState = nowPlayingMovies,
        itemKey = { it.id },
        itemTitle = { it.title },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onMovieClick(it.id) },
        onSeeAllClick = { onSeeAllMovies("now_playing") }
    )

    GenreSection(
        genres = movieGenres,
        selectedGenre = selectedGenre,
        onGenreSelected = { viewModel.loadMoviesByGenre(it) }
    )

    MediaSection(
        title = "Results",
        uiState = moviesByGenre,
        itemKey = { it.id },
        itemTitle = { it.title },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onMovieClick(it.id) }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesContent(
    viewModel: HomeViewModel,
    onSeriesClick: (Int) -> Unit,
    onSeeAllSeries: (String) -> Unit
) {
    val trendingSeries by viewModel.trendingSeries.collectAsState()
    val topRatedSeries by viewModel.topRatedSeries.collectAsState()
    val onTheAirSeries by viewModel.onTheAirSeries.collectAsState()
    val seriesByGenre by viewModel.seriesByGenre.collectAsState()
    val selectedGenre by viewModel.selectedSeriesGenre.collectAsState()

    Spacer(modifier = Modifier.height(8.dp))

    MediaSection(
        title = "🔥 Trending",
        uiState = trendingSeries,
        itemKey = { it.id },
        itemTitle = { it.name },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onSeriesClick(it.id) },
        onSeeAllClick = { onSeeAllSeries("trending") }
    )

    MediaSection(
        title = "🏆 Top Rated",
        uiState = topRatedSeries,
        itemKey = { it.id },
        itemTitle = { it.name },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onSeriesClick(it.id) },
        onSeeAllClick = { onSeeAllSeries("top_rated") }
    )

    MediaSection(
        title = "📡 On The Air",
        uiState = onTheAirSeries,
        itemKey = { it.id },
        itemTitle = { it.name },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onSeriesClick(it.id) },
        onSeeAllClick = { onSeeAllSeries("on_the_air") }
    )

    GenreSection(
        genres = seriesGenres,
        selectedGenre = selectedGenre,
        onGenreSelected = { viewModel.loadSeriesByGenre(it) }
    )

    MediaSection(
        title = "Results",
        uiState = seriesByGenre,
        itemKey = { it.id },
        itemTitle = { it.name },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onSeriesClick(it.id) }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreSection(
    genres: List<Genre>,
    selectedGenre: Genre,
    onGenreSelected: (Genre) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "🎭 Browse by Genre",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedGenre.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(genre.name) },
                        onClick = {
                            onGenreSelected(genre)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
package com.popcinefr.popcinefrapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.data.remote.MediaItem
import com.popcinefr.popcinefrapp.presentation.components.MediaCard
import com.popcinefr.popcinefrapp.presentation.components.MediaSection
import com.popcinefr.popcinefrapp.util.Genre
import com.popcinefr.popcinefrapp.util.UiState
import com.popcinefr.popcinefrapp.util.movieGenres
import com.popcinefr.popcinefrapp.util.seriesGenres

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit,
    onSeeAllMovies: (String) -> Unit,
    onSeeAllSeries: (String) -> Unit,
    onSeeAllMoviesByGenre: (Int, String) -> Unit,
    onSeeAllSeriesByGenre: (Int, String) -> Unit,
    onSeeAllMixed: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val heroMovies by viewModel.heroMovies.collectAsState()
    val heroSeries by viewModel.heroSeries.collectAsState()
    val mixedTrending by viewModel.mixedTrending.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PopCineFR",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
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
                .verticalScroll(rememberScrollState())
        ) {

            // --- Hero Banner ---
            val heroItems = if (selectedTab == HomeTab.MOVIES) {
                heroMovies.map { movie ->
                    HeroItem(
                        id = movie.id,
                        title = movie.title,
                        backdropPath = "https://image.tmdb.org/t/p/w780${movie.backdropPath}",
                        rating = movie.voteAverage,
                        year = movie.releaseDate?.take(4) ?: "",
                        extraInfo = "",
                        genres = emptyList(),
                        mediaType = "movie"
                    )
                }
            } else {
                heroSeries.map { series ->
                    HeroItem(
                        id = series.id,
                        title = series.name,
                        backdropPath = "https://image.tmdb.org/t/p/w780${series.backdropPath}",
                        rating = series.voteAverage,
                        year = series.firstAirDate?.take(4) ?: "",
                        extraInfo = "",
                        genres = emptyList(),
                        mediaType = "series"
                    )
                }
            }

            HeroBanner(
                items = heroItems,
                onItemClick = { id ->
                    if (selectedTab == HomeTab.MOVIES) onMovieClick(id)
                    else onSeriesClick(id)
                },
                onFavoriteClick = { }
            )

            // --- Mixed Trending Section ---
            Spacer(modifier = Modifier.height(20.dp))

            MixedTrendingSection(
                uiState = mixedTrending,
                onItemClick = { item ->
                    if (item.mediaType == "movie") onMovieClick(item.id)
                    else onSeriesClick(item.id)
                },
                onSeeAllClick = onSeeAllMixed
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Gradient Divider with Tab Selector ---
            GradientTabDivider(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Tab Content ---
            if (selectedTab == HomeTab.MOVIES) {
                MoviesContent(
                    viewModel = viewModel,
                    onMovieClick = onMovieClick,
                    onSeeAllMovies = onSeeAllMovies,
                    onSeeAllMoviesByGenre = onSeeAllMoviesByGenre
                )
            } else {
                SeriesContent(
                    viewModel = viewModel,
                    onSeriesClick = onSeriesClick,
                    onSeeAllSeries = onSeeAllSeries,
                    onSeeAllSeriesByGenre = onSeeAllSeriesByGenre
                )
            }
        }
    }
}

// --- Gradient Divider with Tab in the middle ---
@Composable
fun GradientTabDivider(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Gradient lines on both sides
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left gradient line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                primaryColor.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            // Center tab switcher
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                Row {
                    // Movies tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedTab == HomeTab.MOVIES)
                                    primaryColor
                                else
                                    Color.Transparent
                            )
                            .clickable { onTabSelected(HomeTab.MOVIES) }
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Movies",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == HomeTab.MOVIES)
                                FontWeight.Bold
                            else
                                FontWeight.Normal,
                            color = if (selectedTab == HomeTab.MOVIES)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Series tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedTab == HomeTab.SERIES)
                                    primaryColor
                                else
                                    Color.Transparent
                            )
                            .clickable { onTabSelected(HomeTab.SERIES) }
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Series",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == HomeTab.SERIES)
                                FontWeight.Bold
                            else
                                FontWeight.Normal,
                            color = if (selectedTab == HomeTab.SERIES)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Right gradient line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

// --- Mixed Trending Section ---
@Composable
fun MixedTrendingSection(
    uiState: UiState<List<MediaItem>>,
    onItemClick: (MediaItem) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Trending Now",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "See all",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }

        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is UiState.Success -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.data,
                        key = { "${it.mediaType}_${it.id}" }
                    ) { item ->
                        MediaCard(
                            title = item.title,
                            posterPath = item.posterPath,
                            rating = item.voteAverage,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// --- Movies Content ---
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

    MediaSection(
        title = "Now Playing",
        uiState = nowPlayingMovies,
        itemKey = { it.id },
        itemTitle = { it.title },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onMovieClick(it.id) },
        onSeeAllClick = { onSeeAllMovies("now_playing") }
    )

    MediaSection(
        title = "Most Watched",
        uiState = mostWatchedMovies,
        itemKey = { it.id },
        itemTitle = { it.title },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onMovieClick(it.id) },
        onSeeAllClick = { onSeeAllMovies("most_watched") }
    )

    GenreChipsSection(
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
        onItemClick = { onMovieClick(it.id) },
        onSeeAllClick = { onSeeAllMoviesByGenre(selectedGenre.id, selectedGenre.name) }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

// --- Series Content ---
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

    MediaSection(
        title = "On The Air",
        uiState = onTheAirSeries,
        itemKey = { it.id },
        itemTitle = { it.name },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onSeriesClick(it.id) },
        onSeeAllClick = { onSeeAllSeries("on_the_air") }
    )

    MediaSection(
        title = "Most Watched",
        uiState = mostWatchedSeries,
        itemKey = { it.id },
        itemTitle = { it.name },
        itemPoster = { it.posterPath },
        itemRating = { it.voteAverage },
        onItemClick = { onSeriesClick(it.id) },
        onSeeAllClick = { onSeeAllSeries("most_watched") }
    )

    GenreChipsSection(
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
        onItemClick = { onSeriesClick(it.id) },
        onSeeAllClick = { onSeeAllSeriesByGenre(selectedGenre.id, selectedGenre.name) }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

// --- Genre Chips ---
@Composable
fun GenreChipsSection(
    genres: List<Genre>,
    selectedGenre: Genre,
    onGenreSelected: (Genre) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Browse by Genre",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            items(genres) { genre ->
                val isSelected = genre.id == selectedGenre.id
                SuggestionChip(
                    onClick = { onGenreSelected(genre) },
                    label = {
                        Text(
                            text = genre.name,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold
                            else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = if (isSelected) null
                    else SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}
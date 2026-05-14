package com.popcinefr.popcinefrapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.popcinefr.popcinefrapp.data.remote.MediaItem
import com.popcinefr.popcinefrapp.data.remote.MovieDto
import com.popcinefr.popcinefrapp.data.remote.SeriesDto
import com.popcinefr.popcinefrapp.presentation.components.MediaCard
import com.popcinefr.popcinefrapp.presentation.components.MediaSection
import com.popcinefr.popcinefrapp.util.Genre
import com.popcinefr.popcinefrapp.util.UiState
import com.popcinefr.popcinefrapp.util.movieGenres
import com.popcinefr.popcinefrapp.util.seriesGenres
import com.popcinefr.popcinefrapp.util.toImageUrl

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
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
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

            // ── Hero Banner ──────────────────────────────────────────────
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
                }
            )

            // ── Spotlight: Top 3 trending as big numbered cards ──────────
            SpotlightSection(
                uiState = mixedTrending,
                onItemClick = { item ->
                    if (item.mediaType == "movie") onMovieClick(item.id)
                    else onSeriesClick(item.id)
                },
                onSeeAllClick = onSeeAllMixed
            )

            // ── Category Switcher ────────────────────────────────────────
            CategorySwitcher(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            // ── Content ──────────────────────────────────────────────────
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

// ─────────────────────────────────────────────────────────────────────────────
// SPOTLIGHT — Top 3 trending as large numbered cards with blurred backdrop
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SpotlightSection(
    uiState: UiState<List<MediaItem>>,
    onItemClick: (MediaItem) -> Unit,
    onSeeAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Trending Now",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSeeAllClick() }
            ) {
                Text(
                    text = "See all",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is UiState.Success -> {
                // Only top 10 — all as SpotlightCards with rank
                val top10 = uiState.data.take(10)

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(top10) { index, item ->
                        SpotlightCard(
                            item = item,
                            rank = index + 1,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// Large card with rank number and blurred backdrop
@Composable
fun SpotlightCard(
    item: MediaItem,
    rank: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Backdrop
        AsyncImage(
            model = item.backdropPath.toImageUrl("w500"),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.9f),
                            Color.Black.copy(alpha = 0.2f)
                        )
                    )
                )
        )

        // Ghost rank number
        Text(
            text = "$rank",
            fontSize = 90.sp,
            fontWeight = FontWeight.Black,
            color = Color.White.copy(alpha = 0.06f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-8).dp, y = 16.dp)
        )

        // Left side content — paddingEnd reserves space for the poster
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                // 70dp poster width + 12dp padding right + 12dp gap = 94dp reserved
                .padding(start = 12.dp, end = 94.dp, bottom = 12.dp)
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "#$rank",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${"%.1f".format(item.voteAverage)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF5C518)
                )
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .background(Color.White.copy(alpha = 0.5f), CircleShape)
                )
                Text(
                    text = if (item.mediaType == "movie") "Movie" else "Series",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        // Poster — right side, always fixed position
        AsyncImage(
            model = item.posterPath.toImageUrl("w185"),
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
                .width(70.dp)
                .height(105.dp)
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CATEGORY SWITCHER — Icon + label segmented control
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CategorySwitcher(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        // Label
        Text(
            text = "Browse",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryTab(
                label = "Movies",
                icon = Icons.Filled.Movie,
                isSelected = selectedTab == HomeTab.MOVIES,
                onClick = { onTabSelected(HomeTab.MOVIES) },
                modifier = Modifier.weight(1f)
            )
            CategoryTab(
                label = "Series",
                icon = Icons.Filled.Tv,
                isSelected = selectedTab == HomeTab.SERIES,
                onClick = { onTabSelected(HomeTab.SERIES) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CategoryTab(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected)
                    Brush.linearGradient(
                        colors = listOf(
                            primary,
                            primary.copy(alpha = 0.75f)
                        )
                    )
                else
                    Brush.linearGradient(
                        colors = listOf(surfaceVariant, surfaceVariant)
                    )
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MOVIES CONTENT
// ─────────────────────────────────────────────────────────────────────────────
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

    // Now Playing — with a different card style (wider landscape cards)
    SectionWithIcon(
        title = "Now Playing",
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
    GenreChipsSection(
        genres = movieGenres,
        selectedGenre = selectedGenre,
        onGenreSelected = { viewModel.loadMoviesByGenre(it) }
    )

    MediaSection(
        title = "",
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

// ─────────────────────────────────────────────────────────────────────────────
// SERIES CONTENT
// ─────────────────────────────────────────────────────────────────────────────
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

    GenreChipsSection(
        genres = seriesGenres,
        selectedGenre = selectedGenre,
        onGenreSelected = { viewModel.loadSeriesByGenre(it) }
    )

    MediaSection(
        title = "",
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

// ─────────────────────────────────────────────────────────────────────────────
// SHARED COMPONENTS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SectionWithIcon(
    title: String,
    icon: ImageVector,
    onSeeAllClick: (() -> Unit)? = null
) {
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
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if (onSeeAllClick != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSeeAllClick() }
            ) {
                Text(
                    text = "See all",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingRow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
fun ErrorRow(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}

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
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "≡",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
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
                        borderColor = MaterialTheme.colorScheme.outline.copy(
                            alpha = 0.3f
                        )
                    )
                )
            }
        }
    }
}
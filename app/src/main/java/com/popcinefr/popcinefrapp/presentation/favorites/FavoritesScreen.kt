package com.popcinefr.popcinefrapp.presentation.favorites

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.data.local.entity.FavoriteEntity
import com.popcinefr.popcinefrapp.presentation.components.MediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val viewModel: FavoritesViewModel = viewModel()
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()
    val favoriteSeries by viewModel.favoriteSeries.collectAsState()

    // Track whether to show all movies or just first 6
    var showAllMovies by remember { mutableStateOf(false) }
    var showAllSeries by remember { mutableStateOf(false) }

    val displayedMovies = if (showAllMovies) favoriteMovies
    else favoriteMovies.take(6)
    val displayedSeries = if (showAllSeries) favoriteSeries
    else favoriteSeries.take(6)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Favorites",
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp
                        )
                        Text(
                            text = "Your saved cinema shelf",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        if (favoriteMovies.isEmpty() && favoriteSeries.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "No favorites yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Save movies and series you love",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    ),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 32.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Movies section
                if (favoriteMovies.isNotEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        FavoritesSectionHeader(
                            title = "Movies",
                            icon = Icons.Filled.Movie,
                            count = favoriteMovies.size,
                            showSeeAll = !showAllMovies && favoriteMovies.size > 6,
                            onSeeAllClick = { showAllMovies = true }
                        )
                    }

                    items(
                        items = displayedMovies,
                        key = { "movie_${it.id}" }
                    ) { item ->
                        MediaCard(
                            title = item.title,
                            posterPath = item.posterPath,
                            rating = item.voteAverage,
                            onClick = { onMovieClick(item.id) }
                        )
                    }

                    // Show less option
                    if (showAllMovies && favoriteMovies.size > 6) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FavoritesActionBtn(
                                    text = "Show less",
                                    icon = Icons.Filled.KeyboardArrowUp,
                                    onClick = { showAllMovies = false }
                                )
                            }
                        }
                    }
                }

                // Series section
                if (favoriteSeries.isNotEmpty()) {
                    item(span = { GridItemSpan(3) }) {
                        FavoritesSectionHeader(
                            title = "Series",
                            icon = Icons.Filled.Tv,
                            count = favoriteSeries.size,
                            showSeeAll = !showAllSeries && favoriteSeries.size > 6,
                            onSeeAllClick = { showAllSeries = true },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(
                        items = displayedSeries,
                        key = { "series_${it.id}" }
                    ) { item ->
                        MediaCard(
                            title = item.title,
                            posterPath = item.posterPath,
                            rating = item.voteAverage,
                            onClick = { onSeriesClick(item.id) }
                        )
                    }

                    // Show less option
                    if (showAllSeries && favoriteSeries.size > 6) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FavoritesActionBtn(
                                    text = "Show less",
                                    icon = Icons.Filled.KeyboardArrowUp,
                                    onClick = { showAllSeries = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesSectionHeader(
    title: String,
    icon: ImageVector,
    count: Int,
    showSeeAll: Boolean = false,
    onSeeAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(11.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(11.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$count",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (showSeeAll) {
            FavoritesActionBtn(
                text = "See all",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onSeeAllClick
            )
        }
    }
}

@Composable
fun FavoritesActionBtn(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
    }
}

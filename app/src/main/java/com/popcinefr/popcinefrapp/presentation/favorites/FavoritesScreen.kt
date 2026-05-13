package com.popcinefr.popcinefrapp.presentation.favorites

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.data.local.entity.FavoriteEntity
import com.popcinefr.popcinefrapp.presentation.components.MediaCard
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.popcinefr.popcinefrapp.util.toImageUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val viewModel: FavoritesViewModel = viewModel()
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()
    val favoriteSeries by viewModel.favoriteSeries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favorites",
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

        if (favoriteMovies.isEmpty() && favoriteSeries.isEmpty()) {
            // --- Empty State ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
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
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

                if (favoriteMovies.isNotEmpty()) {
                    FavoritesSection(
                        title = "Movies",
                        items = favoriteMovies,
                        onItemClick = { onMovieClick(it.id) }
                    )
                }

                if (favoriteSeries.isNotEmpty()) {
                    FavoritesSection(
                        title = "Series",
                        items = favoriteSeries,
                        onItemClick = { onSeriesClick(it.id) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FavoritesSection(
    title: String,
    items: List<FavoriteEntity>,
    onItemClick: (FavoriteEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Section title with accent bar — same as home
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
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
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = items,
                key = { item -> item.id }
            ) { item ->
                MediaCard(
                    title = item.title,
                    posterPath = item.posterPath,
                    rating = item.voteAverage,
                    onClick = { onItemClick(item) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
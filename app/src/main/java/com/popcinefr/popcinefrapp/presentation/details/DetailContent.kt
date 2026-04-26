package com.popcinefr.popcinefrapp.presentation.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.popcinefr.popcinefrapp.data.remote.GenreDto
import com.popcinefr.popcinefrapp.data.remote.VideoDto
import com.popcinefr.popcinefrapp.util.toImageUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    title: String,
    overview: String,
    posterPath: String?,
    backdropPath: String?,
    voteAverage: Double,
    releaseDate: String?,
    extraInfo: String?,          // runtime for movies, seasons for series
    genres: List<GenreDto>,
    videos: List<VideoDto>,
    isFavorite: Boolean,         // is this already in favorites?
    onFavoriteClick: () -> Unit, // called when user taps the favorite button
    onBackClick: () -> Unit
) {
    // We need context to open external apps like YouTube
    val context = LocalContext.current

    // Find the first YouTube trailer in the videos list
    val trailer = videos.firstOrNull {
        it.type == "Trailer" && it.site == "YouTube"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1) },
                navigationIcon = {
                    // Back button — pops this screen off the stack
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Favorite button in the top bar
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite)
                                Icons.Filled.Favorite
                            else
                                Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            // Red when favorited, default color when not
                            tint = if (isFavorite) Color.Red
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // --- Backdrop Image ---
            AsyncImage(
                model = backdropPath.toImageUrl("w780"),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Title + Poster Row ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Small poster on the left
                AsyncImage(
                    model = posterPath.toImageUrl("w185"),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .height(150.dp)
                )

                // Info on the right
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "⭐ ${"%.1f".format(voteAverage)} / 10",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (releaseDate != null) {
                        Text(
                            text = "📅 $releaseDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (extraInfo != null) {
                        Text(
                            text = extraInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Genres ---
            if (genres.isNotEmpty()) {
                Text(
                    text = "Genres",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontally scrollable genre chips
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    genres.forEach { genre ->
                        // SuggestionChip is a small pill-shaped label
                        SuggestionChip(
                            onClick = {},
                            label = { Text(genre.name) },
                            shape = RoundedCornerShape(50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Overview ---
            Text(
                text = "Overview",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Trailer Button ---
            // Only show if we found a YouTube trailer
            if (trailer != null) {
                Button(
                    onClick = {
                        // Build the YouTube URL and open it
                        val youtubeUrl = "https://www.youtube.com/watch?v=${trailer.key}"
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(youtubeUrl)
                        )
                        // This opens YouTube app if installed
                        // or YouTube in browser if not
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play Trailer"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Trailer on YouTube")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
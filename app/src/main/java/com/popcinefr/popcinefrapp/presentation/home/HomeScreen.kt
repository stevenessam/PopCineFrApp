package com.popcinefr.popcinefrapp.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.presentation.components.MediaSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,   // called when user taps a movie — passes the ID
    onSeriesClick: (Int) -> Unit   // called when user taps a series
) {
    // viewModel() creates or retrieves the ViewModel
    // If the screen rotates, this returns the SAME ViewModel instance — data is safe
    val viewModel: HomeViewModel = viewModel()

    // collectAsState() watches the StateFlow
    // Every time the ViewModel updates the state, this recomposes automatically
    val moviesState by viewModel.moviesState.collectAsState()
    val seriesState by viewModel.seriesState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PopCineFR 🎬",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        // verticalScroll makes the whole screen scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // Trending Movies Section
            // We pass lambdas to tell MediaSection how to extract data from MovieDto
            MediaSection(
                title = "🔥 Trending Movies",
                uiState = moviesState,
                itemKey = { it.id },
                itemTitle = { it.title },
                itemPoster = { it.posterPath },
                itemRating = { it.voteAverage },
                onItemClick = { movie -> onMovieClick(movie.id) }
            )

            // Trending Series Section
            // Same component, different data — this is why we made it generic!
            MediaSection(
                title = "📺 Trending Series",
                uiState = seriesState,
                itemKey = { it.id },
                itemTitle = { it.name },    // ← series uses "name" not "title"
                itemPoster = { it.posterPath },
                itemRating = { it.voteAverage },
                onItemClick = { series -> onSeriesClick(series.id) }
            )
        }
    }
}
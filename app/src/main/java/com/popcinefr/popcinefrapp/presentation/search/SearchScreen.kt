package com.popcinefr.popcinefrapp.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.presentation.components.MediaSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onMovieClick: (Int) -> Unit,
    onSeriesClick: (Int) -> Unit
) {
    val viewModel: SearchViewModel = viewModel()

    // collectAsState watches the StateFlow and recomposes when it changes
    val query by viewModel.searchQuery.collectAsState()
    val moviesState by viewModel.moviesState.collectAsState()
    val seriesState by viewModel.seriesState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Search 🔍",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- Search Bar ---
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search movies or series...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                },
                // Show a clear button only when there is text
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            // --- Results or Empty State ---
            if (query.length < 2) {
                // Show a hint when the user hasn't typed enough yet
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎬",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Search for your favorite",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "movies and series",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Show results in scrollable column
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Movies results section
                    MediaSection(
                        title = "🎬 Movies",
                        uiState = moviesState,
                        itemKey = { it.id },
                        itemTitle = { it.title },
                        itemPoster = { it.posterPath },
                        itemRating = { it.voteAverage },
                        onItemClick = { onMovieClick(it.id) }
                    )

                    // Series results section
                    MediaSection(
                        title = "📺 Series",
                        uiState = seriesState,
                        itemKey = { it.id },
                        itemTitle = { it.name },
                        itemPoster = { it.posterPath },
                        itemRating = { it.voteAverage },
                        onItemClick = { onSeriesClick(it.id) }
                    )
                }
            }
        }
    }
}
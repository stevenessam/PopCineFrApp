package com.popcinefr.popcinefrapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.popcinefr.popcinefrapp.util.UiState

@Composable
fun <T> MediaSection(
    title: String,                          // e.g. "Trending Movies"
    uiState: UiState<List<T>>,             // Loading / Success / Error
    itemKey: (T) -> Int,                   // unique ID for each item
    itemTitle: (T) -> String,              // how to get title from item
    itemPoster: (T) -> String?,            // how to get poster from item
    itemRating: (T) -> Double,             // how to get rating from item
    onItemClick: (T) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Section title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Handle each state
        when (uiState) {

            is UiState.Loading -> {
                // Show a spinner while loading
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                // Show error message
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
                // LazyRow = horizontal scrolling list
                // "Lazy" means it only renders cards visible on screen — efficient!
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.data,
                        key = { itemKey(it) }  // unique key helps Compose update efficiently
                    ) { item ->
                        MediaCard(
                            title = itemTitle(item),
                            posterPath = itemPoster(item),
                            rating = itemRating(item),
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
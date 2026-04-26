package com.popcinefr.popcinefrapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.popcinefr.popcinefrapp.util.UiState

@Composable
fun <T> MediaSection(
    title: String,
    uiState: UiState<List<T>>,
    itemKey: (T) -> Int,
    itemTitle: (T) -> String,
    itemPoster: (T) -> String?,
    itemRating: (T) -> Double,
    onItemClick: (T) -> Unit,
    // Optional — if provided shows the "See All" button
    onSeeAllClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // Section header row — title on left, See All on right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Only show See All if a click handler was provided
            if (onSeeAllClick != null) {
                TextButton(
                    onClick = onSeeAllClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
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
                        key = { itemKey(it) }
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
package com.popcinefr.popcinefrapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popcinefr.popcinefrapp.R
import com.popcinefr.popcinefrapp.presentation.home.components.CategorySwitcher
import com.popcinefr.popcinefrapp.presentation.home.components.MoviesContent
import com.popcinefr.popcinefrapp.presentation.home.components.SeriesContent
import com.popcinefr.popcinefrapp.presentation.home.components.SpotlightSection

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
    val mixedTrending by viewModel.mixedTrending.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = stringResource(R.string.cinema_picks_subtitle),
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

        Column(
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
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Spotlight Section
            SpotlightSection(
                uiState = mixedTrending,
                onItemClick = { item ->
                    if (item.mediaType == "movie") onMovieClick(item.id)
                    else onSeriesClick(item.id)
                },
                onSeeAllClick = onSeeAllMixed
            )

            // Category Switcher
            CategorySwitcher(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )

            // Content
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

package com.popcinefr.popcinefrapp.presentation.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.popcinefr.popcinefrapp.data.remote.MediaItem
import com.popcinefr.popcinefrapp.presentation.components.MediaCard
import com.popcinefr.popcinefrapp.util.Genre
import com.popcinefr.popcinefrapp.util.UiState
import com.popcinefr.popcinefrapp.util.movieGenres
import com.popcinefr.popcinefrapp.util.seriesGenres
import com.popcinefr.popcinefrapp.util.toImageUrl
import kotlinx.coroutines.delay

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
                            text = "PopCine",
                            fontWeight = FontWeight.Black,
                            fontSize = 21.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Cinema picks for tonight",
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
    val infiniteTransition = rememberInfiniteTransition(label = "global_spotlight_fx")
    
    val glowOrbit by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_orbit"
    )

    val borderShift by infiniteTransition.animateFloat(
        initialValue = 0.72f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_shift"
    )

    val shineX by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shine_x"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Trending Now",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "What everyone is watching",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            SeeAllButton(onClick = onSeeAllClick)
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
                val startIndex = remember(top10.size) {
                    if (top10.size <= 1) 0
                    else {
                        val midpoint = Int.MAX_VALUE / 2
                        midpoint - (midpoint % top10.size)
                    }
                }
                val listState = rememberLazyListState(
                    initialFirstVisibleItemIndex = startIndex
                )
                var autoScrolling by remember { mutableStateOf(false) }
                var pauseUntil by remember { mutableLongStateOf(0L) }

                LaunchedEffect(listState) {
                    snapshotFlow { listState.isScrollInProgress }.collect { isScrolling ->
                        if (isScrolling && !autoScrolling) {
                            pauseUntil = System.currentTimeMillis() + 3500L
                        }
                    }
                }

                LaunchedEffect(top10.size) {
                    if (top10.size <= 1) return@LaunchedEffect
                    while (true) {
                        delay(5000L)
                        val remainingPause = pauseUntil - System.currentTimeMillis()
                        if (remainingPause > 0L) delay(remainingPause)
                        if (!listState.isScrollInProgress) {
                            autoScrolling = true
                            try {
                                listState.animateScrollToItem(
                                    listState.firstVisibleItemIndex + 1
                                )
                            } finally {
                                autoScrolling = false
                            }
                        }
                    }
                }

                LazyRow(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        count = if (top10.size > 1) Int.MAX_VALUE else top10.size,
                        key = { index ->
                            val item = top10[index % top10.size]
                            "trending_${index}_${item.mediaType}_${item.id}"
                        }
                    ) { index ->
                        val actualIndex = index % top10.size
                        val item = top10[actualIndex]
                        SpotlightCard(
                            item = item,
                            rank = actualIndex + 1,
                            glowOrbit = glowOrbit,
                            borderShift = borderShift,
                            shineX = shineX,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SpotlightCard(
    item: MediaItem,
    rank: Int,
    glowOrbit: Float,
    borderShift: Float,
    shineX: Float,
    onClick: () -> Unit
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val centerX = with(density) { 143.dp.toPx() }
    val centerY = with(density) { 83.dp.toPx() }
    val orbitRadius = with(density) { 500.dp.toPx() }

    Box(
        modifier = Modifier
            .width(320.dp)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. THE REDESIGNED NEBULA GLOW (3 Layers)
        Box(
            modifier = Modifier.size(width = 320.dp, height = 200.dp)
        ) {
            // Layer 1: Deep Sapphire (Core)
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .align(Alignment.Center)
                    .offset(
                        x = (Math.cos(glowOrbit.toDouble()) * 30).dp,
                        y = (Math.sin(glowOrbit.toDouble()) * 20).dp
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.65f * borderShift),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .blur(45.dp)
            )

            // Layer 2: Vibrant Cyan (Accent)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopStart)
                    .offset(
                        x = (Math.sin(glowOrbit.toDouble() * 1.5) * 40).dp,
                        y = (Math.cos(glowOrbit.toDouble() * 1.5) * 30).dp
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF06B6D4).copy(alpha = 0.35f * borderShift),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .blur(35.dp)
            )

            // Layer 3: Subtle Violet (Depth)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.BottomEnd)
                    .offset(
                        x = (Math.cos(glowOrbit.toDouble() * 0.7) * (-40)).dp,
                        y = (Math.sin(glowOrbit.toDouble() * 0.7) * (-30)).dp
                    )
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6).copy(alpha = 0.25f * borderShift),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
                    .blur(40.dp)
            )
        }

        // 2. The Main Card Body
        Box(
            modifier = Modifier
                .size(width = 286.dp, height = 166.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    spotColor = MaterialTheme.colorScheme.primary
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .border(
                    width = 1.8.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.45f), // Softer Laser Peak
                            MaterialTheme.colorScheme.primary,
                            Color(0xFF0EA5E9), // Premium Blue
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.35f), // Softer Second Streak
                            Color.Transparent
                        ),
                        center = Offset(
                            centerX + Math.cos(glowOrbit.toDouble()).toFloat() * orbitRadius,
                            centerY + Math.sin(glowOrbit.toDouble()).toFloat() * orbitRadius
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .clip(RoundedCornerShape(26.dp))
                .clickable { onClick() }
        ) {
            // Backdrop Image
            AsyncImage(
                model = item.backdropPath.toImageUrl("w500"),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Animated Shine Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f),
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            start = Offset(shineX, 0f),
                            end = Offset(shineX + 150f, 350f)
                        )
                    )
            )

            // Dark Gradient Overlays
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.95f),
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )


            // Info Column
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 14.dp, end = 100.dp, bottom = 14.dp)
            ) {
                // Rank badge
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            width = 1.2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "#$rank",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${"%.1f".format(item.voteAverage)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                    Text(
                        text = if (item.mediaType == "movie") "MOVIE" else "SERIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
            // 3. THE POSTER — Now inside for a cleaner look
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
            ) {
                // Poster Glow
                Box(
                    modifier = Modifier
                        .size(width = 86.dp, height = 125.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                        .blur(15.dp)
                )

                AsyncImage(
                    model = item.posterPath.toImageUrl("w185"),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(78.dp)
                        .height(115.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.2.dp,
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(14.dp),
                            spotColor = Color.Black.copy(alpha = 0.4f)
                        )
                )
            }
        }


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
                .padding(top = 18.dp, bottom = 22.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Browse",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Choose what to explore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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

        Box(
            modifier = modifier
                .height(50.dp)
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
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                )
                .border(
                    width = if (isSelected) 1.dp else 0.dp,
                    color = Color.White.copy(alpha = 0.14f),
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
        GenreDropdownSection(
            genres = movieGenres,
            selectedGenre = selectedGenre,
            onGenreSelected = { viewModel.loadMoviesByGenre(it) },
            onSeeAllClick = { onSeeAllMoviesByGenre(selectedGenre.id, selectedGenre.name) }
        )

        GenreMediaCarousel(
            uiState = moviesByGenre,
            itemKey = { it.id },
            itemTitle = { it.title },
            itemPoster = { it.posterPath },
            itemRating = { it.voteAverage },
            onItemClick = { onMovieClick(it.id) }
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

        GenreDropdownSection(
            genres = seriesGenres,
            selectedGenre = selectedGenre,
            onGenreSelected = { viewModel.loadSeriesByGenre(it) },
            onSeeAllClick = { onSeeAllSeriesByGenre(selectedGenre.id, selectedGenre.name) }
        )

        GenreMediaCarousel(
            uiState = seriesByGenre,
            itemKey = { it.id },
            itemTitle = { it.name },
            itemPoster = { it.posterPath },
            itemRating = { it.voteAverage },
            onItemClick = { onSeriesClick(it.id) }
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.5.sp
                )
                if (title == "Now Playing") {
                    Text(
                        text = "Freshly released movies",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (onSeeAllClick != null) {
            SeeAllButton(onClick = onSeeAllClick)
        }
    }
}

@Composable
fun SeeAllButton(onClick: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
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
                text = "See all",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
        }
    }

@Composable
fun LoadingRow() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(232.dp),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
    }

@Composable
fun ErrorRow(message: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(232.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }

@Composable
fun GenreDropdownSection(
    genres: List<Genre>,
    selectedGenre: Genre,
    onGenreSelected: (Genre) -> Unit,
    onSeeAllClick: () -> Unit
) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Movie,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Discover by Genre",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Explore titles by category",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Second row: Dropdown + See All button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenreDropdown(
                    genres = genres,
                    selectedGenre = selectedGenre,
                    onGenreSelected = onGenreSelected,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                SeeAllButton(onClick = onSeeAllClick)
            }
        }
    }

@Composable
fun GenreDropdown(
    genres: List<Genre>,
    selectedGenre: Genre,
    onGenreSelected: (Genre) -> Unit,
    modifier: Modifier = Modifier
) {
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = modifier) {
            // We wrap the button and menu in a Box to use as a unified anchor.
            // By adding vertical padding to the button itself (external to its background),
            // we create a "dead zone" that DropdownMenu (with offset 0) will respect
            // as the boundary, thus creating a consistent gap in both directions.
            Box {
                // Trigger button
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp) // The gap
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable { expanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedGenre.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded)
                            Icons.Filled.KeyboardArrowUp
                        else
                            Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Fix 1 — offset 0.dp + padding above handles the gap in BOTH directions
                // Fix 2 — shadowElevation(0) + explicit background removes the black window
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 0.dp, y = 0.dp),
                    scrollState = rememberScrollState(),

                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,

                    modifier = Modifier
                        .heightIn(max = 260.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    genres.forEach { genre ->
                        val isSelected = genre.id == selectedGenre.id

                        DropdownMenuItem(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    else
                                        Color.Transparent
                                ),
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = genre.name,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold
                                        else FontWeight.Normal,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            },
                            onClick = {
                                expanded = false
                                onGenreSelected(genre)
                            }
                        )
                    }
                }
            }
        }
    }

@Composable
fun <T> GenreMediaCarousel(
    uiState: UiState<List<T>>,
    itemKey: (T) -> Int,
    itemTitle: (T) -> String,
    itemPoster: (T) -> String?,
    itemRating: (T) -> Double,
    onItemClick: (T) -> Unit
) {
        when (uiState) {
            is UiState.Loading -> LoadingRow()
            is UiState.Error -> ErrorRow(uiState.message)
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
    }


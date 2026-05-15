package com.popcinefr.popcinefrapp.presentation.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.popcinefr.popcinefrapp.R
import com.popcinefr.popcinefrapp.data.remote.MediaItem
import com.popcinefr.popcinefrapp.util.UiState
import com.popcinefr.popcinefrapp.util.toImageUrl
import kotlinx.coroutines.delay
import androidx.compose.runtime.snapshotFlow
import com.popcinefr.popcinefrapp.presentation.components.SeeAllButton

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
                            text = stringResource(R.string.trending_now),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.trending_subtitle),
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
    val density = LocalDensity.current
    val centerX = with(density) { 143.dp.toPx() }
    val centerY = with(density) { 83.dp.toPx() }
    val orbitRadius = with(density) { 500.dp.toPx() }

    Box(
        modifier = Modifier
            .width(320.dp)
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Nebula Glow
        Box(modifier = Modifier.size(width = 320.dp, height = 200.dp)) {
            // Layer 1
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
            // Layer 2
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
            // Layer 3
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

        // Main Card Body
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
                            Color.White.copy(alpha = 0.45f),
                            MaterialTheme.colorScheme.primary,
                            Color(0xFF0EA5E9),
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.35f),
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

            // Dark Overlays
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
            Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.95f), Color.Black.copy(alpha = 0.4f), Color.Transparent))))

            // Info Column
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 14.dp, end = 100.dp, bottom = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "#$rank", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.5.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.title, fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(12.dp))
                    Text(text = "%.1f".format(item.voteAverage), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Box(modifier = Modifier.size(3.dp).background(Color.White.copy(alpha = 0.4f), CircleShape))
                    Text(text = if (item.mediaType == "movie") stringResource(R.string.movies).uppercase() else stringResource(R.string.series).uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.6f))
                }
            }

            // Poster
            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) {
                Box(modifier = Modifier.size(width = 86.dp, height = 125.dp).align(Alignment.Center).background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), Color.Transparent)), CircleShape).blur(15.dp))
                AsyncImage(
                    model = item.posterPath.toImageUrl("w185"),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(78.dp).height(115.dp).clip(RoundedCornerShape(14.dp)).border(1.2.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp)).shadow(6.dp, RoundedCornerShape(14.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                )
            }
        }
    }
}


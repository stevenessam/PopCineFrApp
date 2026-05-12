package com.popcinefr.popcinefrapp.presentation.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.popcinefr.popcinefrapp.data.remote.CastMemberDto
import com.popcinefr.popcinefrapp.data.remote.GenreDto
import com.popcinefr.popcinefrapp.data.remote.VideoDto
import com.popcinefr.popcinefrapp.presentation.components.VidkingPlayer
import com.popcinefr.popcinefrapp.presentation.components.YoutubePlayer
import com.popcinefr.popcinefrapp.util.toImageUrl
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    tmdbId: Int,
    isMovie: Boolean,
    title: String,
    overview: String,
    posterPath: String?,
    backdropPath: String?,
    voteAverage: Double,
    releaseDate: String?,
    extraInfo: String?,
    genres: List<GenreDto>,
    videos: List<VideoDto>,
    cast: List<CastMemberDto>,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val trailer = videos.firstOrNull {
        it.type == "Trailer" && it.site == "YouTube"
    }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var trailerSectionY by remember { mutableIntStateOf(0) }
    var watchSectionY by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onFavoriteClick) {
                            Icon(
                                imageVector = if (isFavorite)
                                    Icons.Filled.Favorite
                                else
                                    Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            // --- Hero Section ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                AsyncImage(
                    model = backdropPath.toImageUrl("w780"),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    AsyncImage(
                        model = posterPath.toImageUrl("w185"),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(90.dp)
                            .height(135.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "⭐ ${"%.1f".format(voteAverage)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (releaseDate != null) {
                                Text(
                                    text = "•",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = releaseDate.take(4),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (extraInfo != null) {
                                Text(
                                    text = "•",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = extraInfo,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            genres.take(3).forEach { genre ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(genre.name, fontSize = 10.sp) },
                                    shape = RoundedCornerShape(50),
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // --- Action Buttons ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            scrollState.animateScrollTo(watchSectionY)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("▶ Watch Now", fontWeight = FontWeight.Bold)
                }

                if (trailer != null) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(trailerSectionY)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🎬 Trailer", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Overview ---
            SectionTitle(title = "Overview")
            Text(
                text = overview,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Cast ---
            if (cast.isNotEmpty()) {
                SectionTitle(title = "Cast")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = cast.take(15),
                        key = { it.id }
                    ) { member ->
                        CastCard(member = member)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Trailer Section ---
            if (trailer != null) {
                Box(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        trailerSectionY = coordinates.positionInRoot().y.toInt()
                    }
                ) {
                    Column {
                        SectionTitle(title = "Trailer")
                        YoutubePlayer(
                            youtubeKey = trailer.key,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // --- Watch Section ---
            Box(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    watchSectionY = coordinates.positionInRoot().y.toInt()
                }
            ) {
                Column {
                    SectionTitle(title = "Watch")
                    Text(
                        text = if (isMovie) "Stream the full movie"
                        else "Stream with episode selector",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VidkingPlayer(
                        tmdbId = tmdbId,
                        isMovie = isMovie,
                        thumbnailPath = backdropPath,   // ← add this
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
}

@Composable
fun CastCard(member: CastMemberDto) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (member.profilePath != null) {
                AsyncImage(
                    model = member.profilePath.toImageUrl("w185"),
                    contentDescription = member.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = member.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 14.sp
        )

        Text(
            text = member.character,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 13.sp
        )
    }
}
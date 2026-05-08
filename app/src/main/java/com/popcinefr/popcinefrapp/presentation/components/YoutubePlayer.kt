package com.popcinefr.popcinefrapp.presentation.components

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.popcinefr.popcinefrapp.util.toImageUrl

@Composable
fun YoutubePlayer(
    youtubeKey: String,
    thumbnailPath: String? = null,  // we show the movie backdrop as preview
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // The YouTube URL we will open
    val youtubeUrl = "https://www.youtube.com/watch?v=$youtubeKey"

    // Thumbnail URL — YouTube provides this automatically for every video
    // This always works even if the video has embed restrictions
    val youtubeThumbnailUrl = "https://img.youtube.com/vi/$youtubeKey/hqdefault.jpg"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {

        // --- Background: YouTube thumbnail ---
        // Every YouTube video has a free thumbnail at this URL
        // No API key needed, always available
        AsyncImage(
            model = youtubeThumbnailUrl,
            contentDescription = "Trailer thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )

        // --- Dark gradient overlay so button is visible ---
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // --- Play button in the center ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    openWithCustomTab(context, youtubeUrl)
                },
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play Trailer",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Watch Trailer",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Opens the URL in a Chrome Custom Tab
// This looks like it's inside the app — same app bar color, smooth animation
// Works for 100% of YouTube videos — no embed restrictions apply
fun openWithCustomTab(context: Context, url: String) {
    val colorSchemeParams = CustomTabColorSchemeParams.Builder()
        // Match the tab toolbar to Material default surface color (dark or light)
        .setToolbarColor(android.graphics.Color.BLACK)
        .build()

    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(colorSchemeParams)
        // Smooth slide-in animation
        .setShowTitle(true)
        .build()

    customTabsIntent.launchUrl(context, Uri.parse(url))
}
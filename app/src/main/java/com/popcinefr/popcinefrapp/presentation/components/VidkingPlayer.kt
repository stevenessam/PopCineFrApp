package com.popcinefr.popcinefrapp.presentation.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage

// Custom WebView that takes full ownership of all touch events
class TouchableWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        requestDisallowInterceptTouchEvent(true)
        parent?.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        requestDisallowInterceptTouchEvent(true)
        return false
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VidkingPlayer(
    tmdbId: Int,
    isMovie: Boolean,
    thumbnailPath: String? = null,
    season: Int = 1,
    episode: Int = 1,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity

    // State that controls whether to show the thumbnail+play button
    // or the actual WebView player
    // false = show thumbnail with play button overlay
    // true = show the real WebView player
    var isPlayerLoaded by remember { mutableStateOf(false) }

    // autoPlay=true because the user already tapped our custom play button
    // so we start playing immediately when the WebView loads
    val embedUrl = if (isMovie) {
        "https://www.vidking.net/embed/movie/$tmdbId?color=e50914&autoPlay=true"
    } else {
        "https://www.vidking.net/embed/tv/$tmdbId/$season/$episode?color=e50914&autoPlay=true&nextEpisode=true&episodeSelector=true"
    }

    // YouTube thumbnail as preview — always available for any TMDB content
    // We use the TMDB poster as fallback background
    val thumbnailUrl = "https://image.tmdb.org/t/p/w780$thumbnailPath"

    val webView = remember {
        TouchableWebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }

            webChromeClient = object : WebChromeClient() {

                private var customView: View? = null
                private var customViewCallback: CustomViewCallback? = null
                private var originalSystemUiVisibility = 0

                override fun onShowCustomView(
                    view: View?,
                    callback: CustomViewCallback?
                ) {
                    if (customView != null) {
                        onHideCustomView()
                        return
                    }

                    customView = view
                    customViewCallback = callback
                    originalSystemUiVisibility =
                        activity.window.decorView.systemUiVisibility

                    val decorView = activity.window.decorView as ViewGroup
                    decorView.addView(
                        customView,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )

                    activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                    activity.window.addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    )
                }

                override fun onHideCustomView() {
                    val decorView = activity.window.decorView as ViewGroup
                    decorView.removeView(customView)
                    customView = null

                    activity.window.decorView.systemUiVisibility =
                        originalSystemUiVisibility

                    activity.window.clearFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    )

                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                }
            }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                loadWithOverviewMode = true
                useWideViewPort = true
                allowContentAccess = true
                allowFileAccess = true
                mixedContentMode =
                    android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (!isPlayerLoaded) {
            // --- Thumbnail + Play Button Overlay ---
            // This is what the user sees before tapping play
            // It's a normal Compose button — no WebView conflict at all
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Backdrop as background
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = "Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )

                // Play button — this is a regular Compose button
                // No WebView = no touch conflict = works perfectly
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            // User tapped our button — NOW load the WebView
                            // autoPlay=true means it starts immediately
                            webView.loadUrl(embedUrl)
                            isPlayerLoaded = true
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = if (isMovie) "Play Movie" else "Play Series",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.then(
                            Modifier.background(Color.Transparent)
                        )
                    )
                }
            }
        } else {
            // --- Real WebView Player ---
            // Only shown after user taps play
            // autoPlay=true so it starts immediately without needing
            // to tap anything inside the WebView
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
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
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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

// Known ad domains to block
private val AD_DOMAINS = listOf(
    "doubleclick.net",
    "googlesyndication.com",
    "googleadservices.com",
    "adnxs.com",
    "advertising.com",
    "outbrain.com",
    "taboola.com",
    "exoclick.com",
    "trafficjunky.net",
    "juicyads.com",
    "hilltopads.net",
    "adsterra.com",
    "propellerads.com"
)

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

    var isPlayerLoaded by remember { mutableStateOf(false) }

    val embedUrl = if (isMovie) {
        "https://www.vidking.net/embed/movie/$tmdbId?color=e50914&autoPlay=true"
    } else {
        "https://www.vidking.net/embed/tv/$tmdbId/$season/$episode?color=e50914&autoPlay=true&nextEpisode=true&episodeSelector=true"
    }

    val thumbnailUrl = "https://image.tmdb.org/t/p/w780$thumbnailPath"

    val webView = remember {
        TouchableWebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false

                    // Block known ad domains
                    val isAd = AD_DOMAINS.any { domain -> url.contains(domain) }
                    if (isAd) return true // block the request

                    // Block popups — any navigation away from vidking
                    val isVidking = url.contains("vidking.net")
                    if (!isVidking) return true // block external navigations

                    return false // allow vidking URLs
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
                // Disable opening new windows — prevents popup ads
                javaScriptCanOpenWindowsAutomatically = false
                setSupportMultipleWindows(false)
            }
        }
    }

    // BackHandler intercepts the phone back button when player is loaded
    // Instead of going back in the app, we go back inside the WebView
    if (isPlayerLoaded) {
        BackHandler {
            if (webView.canGoBack()) {
                webView.goBack()
            }
            // If WebView can't go back, do nothing
            // This prevents accidentally going back to home
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        if (!isPlayerLoaded) {
            // Thumbnail + Play Button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = "Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
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

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isMovie) "Play Movie" else "Play Series",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
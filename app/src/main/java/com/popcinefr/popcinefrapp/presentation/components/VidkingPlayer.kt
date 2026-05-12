package com.popcinefr.popcinefrapp.presentation.components

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VidkingPlayer(
    tmdbId: Int,
    isMovie: Boolean,
    season: Int = 1,
    episode: Int = 1,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity

    val embedUrl = if (isMovie) {
        "https://www.vidking.net/embed/movie/$tmdbId?color=e50914&autoPlay=false"
    } else {
        "https://www.vidking.net/embed/tv/$tmdbId/$season/$episode?color=e50914&autoPlay=false&nextEpisode=true&episodeSelector=true"
    }

    // We remember the WebView so it doesn't reload on recomposition
    val webView = remember {
        WebView(context).apply {
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
                    // If already showing a custom view dismiss it first
                    if (customView != null) {
                        onHideCustomView()
                        return
                    }

                    customView = view
                    customViewCallback = callback

                    // Save original system UI visibility to restore later
                    originalSystemUiVisibility = activity.window.decorView.systemUiVisibility

                    // Add the fullscreen view on top of everything
                    val decorView = activity.window.decorView as ViewGroup
                    decorView.addView(
                        customView,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )

                    // Hide system bars for true fullscreen
                    activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                    // Keep screen on while watching
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                override fun onHideCustomView() {
                    // Remove the fullscreen view
                    val decorView = activity.window.decorView as ViewGroup
                    decorView.removeView(customView)
                    customView = null

                    // Restore original system UI
                    activity.window.decorView.systemUiVisibility =
                        originalSystemUiVisibility

                    // Allow screen to turn off again
                    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                    // Notify the WebView the custom view is hidden
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
            }

            loadUrl(embedUrl)
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}
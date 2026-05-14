package com.popcinefr.popcinefrapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.popcinefr.popcinefrapp.presentation.navigation.NavGraph
import com.popcinefr.popcinefrapp.presentation.splash.SplashScreen
import com.popcinefr.popcinefrapp.ui.theme.PopCineFrAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {
            PopCineFrAppTheme {
                val view = LocalView.current
                val backgroundColor = MaterialTheme.colorScheme.background

                SideEffect {
                    val window = (view.context as ComponentActivity).window
                    window.statusBarColor = backgroundColor.toArgb()
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightStatusBars =
                        backgroundColor.red > 0.5f
                }

                // Controls whether we show splash or main app
                var showSplash by remember { mutableStateOf(true) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplash) {
                        SplashScreen(
                            onFinished = { showSplash = false }
                        )
                    } else {
                        NavGraph()
                    }
                }
            }
        }
    }
}
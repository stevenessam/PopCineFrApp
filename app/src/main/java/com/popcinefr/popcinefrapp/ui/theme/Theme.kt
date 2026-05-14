package com.popcinefr.popcinefrapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = PopCineDarkBackground,
    surface = PopCineDarkSurface,
    surfaceVariant = PopCineDarkSurfaceVariant,
    onPrimary = Color.White,
    onBackground = Color(0xFFEAF1FF),
    onSurface = Color(0xFFEAF1FF),
    onSurfaceVariant = Color(0xFFB7C8E8)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = PopCineLightBackground,
    surface = PopCineLightSurface,
    surfaceVariant = PopCineLightSurfaceVariant,
    onPrimary = Color.White,
    onBackground = Color(0xFF07111F),
    onSurface = Color(0xFF07111F),
    onSurfaceVariant = Color(0xFF4B5D78)
)

@Composable
fun PopCineFrAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

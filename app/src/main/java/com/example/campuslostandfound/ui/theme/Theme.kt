package com.example.campuslostandfound.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Black,
    onPrimary = White,
    secondary = SentBubbleBlue,
    background = White,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    surfaceVariant = PlaceholderGray,
    onSurfaceVariant = Black,
    outline = OutlineGray,
    error = DiscardRed,
)

@Composable
fun CampusLostAndFoundTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content,
    )
}

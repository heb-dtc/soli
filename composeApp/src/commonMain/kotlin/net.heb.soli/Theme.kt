package net.heb.soli

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val red = Color(0xFFef265b)
val yellow = Color(0xFFecf589)
val purple = Color(0xFF7522c8)
val green = Color(0xFF82ee69)

private val LightColors = lightColorScheme(
    primary = red,
    secondary = yellow,
    surface = red,
    onSurface = yellow,
    surfaceContainerLow = yellow,
    onSurfaceVariant = red
)

private val DarkColors = lightColorScheme(
    primary = purple,
    secondary = green,
    surface = purple,
    onSurface = green,
    surfaceContainerLow = green,
    onSurfaceVariant = purple
)

@Composable
fun SoliTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit,
) {
    val colors = if (!useDarkTheme) LightColors else DarkColors

    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
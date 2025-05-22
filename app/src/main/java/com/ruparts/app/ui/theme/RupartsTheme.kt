package com.ruparts.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.ruparts.app.R

@Composable
fun RupartsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val primary = colorResource(id = R.color.purple)
    val primaryContainer = Color(primary.value).copy(alpha = 0.1f)
    val onPrimaryContainer = primary
    val secondary = colorResource(id = R.color.blue_700)
    val tertiary = colorResource(id = R.color.blue_900)
    val background = colorResource(id = R.color.background_color)
    val surface = colorResource(id = R.color.surface_color)
    val onSurface = colorResource(id = R.color.text_color)
    val outline = colorResource(id = R.color.light_gray)
    
    val colorScheme = lightColorScheme(
        primary = primary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        tertiary = tertiary,
        background = background,
        surface = surface,
        onSurface = onSurface,
        outline = outline,
        surfaceVariant = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

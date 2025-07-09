package com.ruparts.app.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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
    // Primary colors
    val primary = colorResource(id = R.color.primary)
    val onPrimary = colorResource(id = R.color.onPrimary)
    val primaryDark = colorResource(id = R.color.purple_dark)
    val primaryContainer = colorResource(id = R.color.purple_middle)
    val onPrimaryContainer = primaryDark

    // Secondary colors
    val secondary = colorResource(id = R.color.purple)
    val secondaryContainer = colorResource(id = R.color.light_purple)
    val onSecondaryContainer = primaryDark

    // Tertiary colors
    val tertiary = colorResource(id = R.color.purple_middle)
    val tertiaryContainer = colorResource(id = R.color.light_purple)
    val onTertiaryContainer = primaryDark

    // Background and surface colors
    val background = colorResource(id = R.color.based_background)
    val surface = colorResource(id = R.color.surface)
    val surfaceVariant = colorResource(id = R.color.light_purple)
    val onSurface = colorResource(id = R.color.onSurface)
    val onSurfaceVariant = primaryDark

    // Other colors
    val outline = colorResource(id = R.color.purple)

    val colorScheme = when (darkTheme) {
        true -> darkColorScheme()
        false -> lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = onSecondaryContainer,
            tertiary = tertiary,
            onTertiary = primaryDark,
            tertiaryContainer = tertiaryContainer,
            onTertiaryContainer = onTertiaryContainer,
            background = background,
            onBackground = Color.Black,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

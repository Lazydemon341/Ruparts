package com.ruparts.app.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ruparts.app.R

@Composable
fun RupartsTheme(
    content: @Composable () -> Unit
) {
    // Primary colors
    val primary = colorResource(id = R.color.primary)
    val onPrimary = colorResource(id = R.color.onPrimary)
    val primaryDark = colorResource(id = R.color.purple_dark)
    val primaryContainer = colorResource(id = R.color.purple_middle)
    val onPrimaryContainer = primaryDark

    // Secondary colors
    val secondary = colorResource(id = R.color.secondary)
    val secondaryContainer = colorResource(id = R.color.secondaryContainer)
    val onSecondaryContainer = colorResource(id = R.color.onSecondaryContainer)

    // Tertiary colors
    val tertiary = colorResource(id = R.color.purple_middle)
    val tertiaryContainer = colorResource(id = R.color.light_purple)
    val onTertiaryContainer = primaryDark

    // Background and surface colors
    val background = colorResource(id = R.color.based_background)
    val surface = colorResource(id = R.color.surface)
    val surfaceVariant = colorResource(id = R.color.light_purple)
    val surfaceContainer = colorResource(id = R.color.surfaceContainer)
    val onSurface = colorResource(id = R.color.onSurface)
    val onSurfaceVariant = colorResource(id = R.color.onSurfaceVariant)

    // Error colors
    val error = colorResource(id = R.color.error)
    val onError = colorResource(id = R.color.onError)
    
    // Other colors
    val outline = colorResource(id = R.color.purple)

    val colorScheme = lightColorScheme(
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
        surfaceContainer = surfaceContainer,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        error = error,
        onError = onError,
    )

    val typography = Typography(
        // Title styles - M3/title/large-emphasized: Font(family: "Roboto", style: Medium, size: 22, weight: 500, lineHeight: 28)
        titleLarge = TextStyle(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight(500), // Medium weight
            letterSpacing = 0.sp,
        ),
        titleMedium = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.5.sp, // M3/body/large-emphasized uses tracking 0.5
        ),
        titleSmall = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.1.sp,
        ),

        // Body styles - from Figma design system
        bodyLarge = TextStyle(
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight(400), // M3/body/large: Regular weight
            letterSpacing = 0.5.sp, // Updated from Figma
        ),
        bodyMedium = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(400), // M3/body/medium: Regular weight
            letterSpacing = 0.25.sp, // Static/Body Medium/Tracking: 0.25
        ),
        bodySmall = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.4.sp,
        ),

        // Label styles
        labelLarge = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.1.sp,
        ),
        labelMedium = TextStyle(
            fontSize = 12.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontSize = 11.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight(500),
            letterSpacing = 0.5.sp,
        ),

        // Headline styles
        headlineLarge = TextStyle(
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.sp,
        ),
        headlineMedium = TextStyle(
            fontSize = 28.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.sp,
        ),
        headlineSmall = TextStyle(
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.sp,
        ),

        // Display styles
        displayLarge = TextStyle(
            fontSize = 57.sp,
            lineHeight = 64.sp,
            fontWeight = FontWeight(400),
            letterSpacing = (-0.25).sp,
        ),
        displayMedium = TextStyle(
            fontSize = 45.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.sp,
        ),
        displaySmall = TextStyle(
            fontSize = 36.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight(400),
            letterSpacing = 0.sp,
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

package com.mishin.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Mishin Material3 color schemes derived from the brand palette.
 */
private val DarkColorScheme = darkColorScheme(
    primary = MishinColors.Gold,
    onPrimary = MishinColors.DarkBackground,
    primaryContainer = MishinColors.GoldDark,
    onPrimaryContainer = MishinColors.DarkOnBackground,

    secondary = MishinColors.GoldLight,
    onSecondary = MishinColors.DarkBackground,

    background = MishinColors.DarkBackground,
    onBackground = MishinColors.DarkOnBackground,

    surface = MishinColors.DarkSurface,
    onSurface = MishinColors.DarkOnSurface,
    surfaceVariant = MishinColors.DarkSurfaceElevated,
    onSurfaceVariant = MishinColors.DarkOnSurfaceVariant,

    error = MishinColors.Error,
    onError = MishinColors.DarkOnBackground,

    outline = MishinColors.DarkOnSurfaceVariant.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = MishinColors.GoldDark,
    onPrimary = MishinColors.LightBackground,
    primaryContainer = MishinColors.Gold,
    onPrimaryContainer = MishinColors.LightOnBackground,

    secondary = MishinColors.DarkBackground,
    onSecondary = MishinColors.LightBackground,

    background = MishinColors.LightBackground,
    onBackground = MishinColors.LightOnBackground,

    surface = MishinColors.LightSurface,
    onSurface = MishinColors.LightOnSurface,
    surfaceVariant = MishinColors.LightSurfaceElevated,
    onSurfaceVariant = MishinColors.LightOnSurfaceVariant,

    error = MishinColors.Error,
    onError = MishinColors.LightBackground,

    outline = MishinColors.LightOnSurfaceVariant.copy(alpha = 0.3f)
)

private val MishinMaterialTypography = Typography(
    headlineLarge = MishinTypography.headlineLarge,
    headlineMedium = MishinTypography.headlineMedium,
    headlineSmall = MishinTypography.headlineSmall,
    titleLarge = MishinTypography.titleLarge,
    titleMedium = MishinTypography.titleMedium,
    bodyLarge = MishinTypography.bodyLarge,
    bodyMedium = MishinTypography.bodyMedium,
    bodySmall = MishinTypography.bodySmall,
    labelLarge = MishinTypography.labelLarge,
    labelMedium = MishinTypography.labelMedium
)

/**
 * Main theme composable for the Mishin app.
 * Applies dark/light scheme + Mishin typography.
 * Sets the status bar color to match the background.
 */
@Composable
fun MishinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Update status bar appearance
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MishinMaterialTypography,
        content = content
    )
}

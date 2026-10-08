package com.mishin.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Mishin color palette — derived from the mockups.
 *
 * Dark mode: deep navy backgrounds with warm golden accents.
 * Light mode: warm cream backgrounds with navy/dark accents.
 */
object MishinColors {

    // === Brand Colors ===

    /** Primary gold/yellow accent — buttons, active tabs, highlights */
    val Gold = Color(0xFFD4A843)
    val GoldLight = Color(0xFFE8C468)
    val GoldDark = Color(0xFFB08930)

    // === Dark Theme ===

    /** Main background — deep navy */
    val DarkBackground = Color(0xFF0D1B2A)
    /** Surface — slightly lighter navy for cards */
    val DarkSurface = Color(0xFF1B2838)
    /** Elevated surface — for dialogs, drawers */
    val DarkSurfaceElevated = Color(0xFF243447)

    val DarkOnBackground = Color(0xFFF5F0E8)
    val DarkOnSurface = Color(0xFFF5F0E8)
    val DarkOnSurfaceVariant = Color(0xFFB0A899)

    // === Light Theme ===

    /** Main background — warm cream */
    val LightBackground = Color(0xFFF5F0E8)
    /** Surface — white/off-white for cards */
    val LightSurface = Color(0xFFFFFFFF)
    /** Elevated surface */
    val LightSurfaceElevated = Color(0xFFFAF7F2)

    val LightOnBackground = Color(0xFF0D1B2A)
    val LightOnSurface = Color(0xFF1B2838)
    val LightOnSurfaceVariant = Color(0xFF5A6577)

    // === Status Colors ===

    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFE53935)
    val Info = Color(0xFF2196F3)

    // === Card Colors ===

    /** Card background in dark mode — warm off-white (as shown in mockups) */
    val CardLight = Color(0xFFFBF8F3)
    val CardDark = Color(0xFF1E2D3D)
}

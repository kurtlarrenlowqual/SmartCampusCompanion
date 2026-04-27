package com.example.smartcampuscompanion.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary        = Purple80,
    secondary      = PurpleGrey80,
    tertiary       = Pink80,
    background     = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surface        = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onPrimary      = androidx.compose.ui.graphics.Color.Black,
    onSecondary    = androidx.compose.ui.graphics.Color.Black,
    onBackground   = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    onSurface      = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0)
)

private val LightColorScheme = lightColorScheme(
    primary        = Purple40,
    secondary      = PurpleGrey40,
    tertiary       = Pink40,
    background     = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    surface        = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onPrimary      = androidx.compose.ui.graphics.Color.White,
    onSecondary    = androidx.compose.ui.graphics.Color.White,
    onBackground   = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface      = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F)
)

@Composable
fun SmartCampusCompanionTheme(
    darkTheme: Boolean = false,   // controlled by UserPrefsDataStore, NOT system
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
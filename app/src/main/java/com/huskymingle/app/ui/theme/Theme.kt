package com.huskymingle.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = HuskyRed,
    onPrimary = White,
    primaryContainer = HuskyRedLight,
    onPrimaryContainer = White,
    secondary = HuskyGold,
    onSecondary = NearBlack,
    secondaryContainer = HuskyGoldDark,
    onSecondaryContainer = White,
    tertiary = HuskyCoral,
    onTertiary = White,
    background = OffWhite,
    onBackground = NearBlack,
    surface = SurfaceLight,
    onSurface = NearBlack,
    surfaceVariant = SurfaceMutedLight,
    onSurfaceVariant = DarkGray,
    error = HuskyCoral,
    onError = White,
    outline = LightGray,
    outlineVariant = LightGray,
)

private val DarkColorScheme = darkColorScheme(
    primary = HuskyRedLight,
    onPrimary = White,
    primaryContainer = HuskyRedDeep,
    onPrimaryContainer = White,
    secondary = HuskyGold,
    onSecondary = NearBlack,
    secondaryContainer = HuskyGoldDark,
    onSecondaryContainer = White,
    tertiary = HuskyCoral,
    onTertiary = White,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceMutedDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceMutedDark,
    error = HuskyCoral,
    onError = White,
    outline = SurfaceVariantDark,
    outlineVariant = SurfaceVariantDark,
)

/**
 * Exposes Husky-specific design tokens (spacing, radius) alongside MaterialTheme.
 * Read via `HMTheme.spacing.md` / `HMTheme.radius.lg`.
 */
object HMTheme {
    val spacing: HMSpacing
        @Composable
        get() = LocalHMSpacing.current

    val radius: HMRadius
        @Composable
        get() = LocalHMRadius.current
}

private val LocalHMSpacing = staticCompositionLocalOf { HMSpacing() }
private val LocalHMRadius = staticCompositionLocalOf { HMRadius() }

@Composable
fun HuskyMingleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalHMSpacing provides HMSpacing(),
        LocalHMRadius provides HMRadius(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = HMTypography,
            content = content,
        )
    }
}

package com.huskymingle.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
    background = OffWhite,
    onBackground = NearBlack,
    surface = White,
    onSurface = NearBlack,
    surfaceVariant = LightGray,
    onSurfaceVariant = DarkGray,
    error = HuskyRed,
    outline = MediumGray
)

private val DarkColorScheme = darkColorScheme(
    primary = HuskyRedLight,
    onPrimary = White,
    primaryContainer = HuskyRedDark,
    onPrimaryContainer = White,
    secondary = HuskyGold,
    onSecondary = NearBlack,
    secondaryContainer = HuskyGoldDark,
    onSecondaryContainer = White,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = MediumGray,
    error = HuskyRedLight,
    outline = MediumGray
)

@Composable
fun HuskyMingleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

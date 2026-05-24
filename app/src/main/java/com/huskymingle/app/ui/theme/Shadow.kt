package com.huskymingle.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Husky shadow presets — mirrors the AppTheme.Shadow enum on iOS
 * (card / float / brand) so cards, sheets, and primary CTAs share a
 * single elevation language.
 */
object HMShadow {
    val cardElevation: Dp = 4.dp
    val floatElevation: Dp = 12.dp
    val brandElevation: Dp = 8.dp
}

@Composable
fun Modifier.hmCardShadow(radius: Dp = 16.dp): Modifier =
    this.shadow(
        elevation = HMShadow.cardElevation,
        shape = RoundedCornerShape(radius),
        ambientColor = Color.Black.copy(alpha = 0.08f),
        spotColor = Color.Black.copy(alpha = 0.08f),
    )

@Composable
fun Modifier.hmFloatShadow(radius: Dp = 20.dp): Modifier =
    this.shadow(
        elevation = HMShadow.floatElevation,
        shape = RoundedCornerShape(radius),
        ambientColor = Color.Black.copy(alpha = 0.14f),
        spotColor = Color.Black.copy(alpha = 0.14f),
    )

@Composable
fun Modifier.hmBrandShadow(radius: Dp = 16.dp): Modifier =
    this.shadow(
        elevation = HMShadow.brandElevation,
        shape = RoundedCornerShape(radius),
        ambientColor = HuskyRed.copy(alpha = 0.30f),
        spotColor = HuskyRed.copy(alpha = 0.30f),
    )

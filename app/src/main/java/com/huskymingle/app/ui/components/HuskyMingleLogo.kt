package com.huskymingle.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyGold
import com.huskymingle.app.ui.theme.HuskyRed
import com.huskymingle.app.ui.theme.HuskyRedDeep
import com.huskymingle.app.ui.theme.White
import com.huskymingle.app.ui.theme.hmBrandShadow

/**
 * HM monogram on a husky-red gradient circle with a gold accent dot.
 * Optional "HUSKYMINGLE" wordmark beneath. Mirrors the iOS HuskyMingleLogoView.
 */
@Composable
fun HuskyMingleLogo(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    showWordmark: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .hmBrandShadow(radius = size / 2),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(HuskyRed, HuskyRedDeep),
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "HM",
                    color = White,
                    fontSize = (size.value * 0.42f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                )
            }

            // Gold accent dot (bottom-right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = size * 0.08f, bottom = size * 0.08f)
                    .size(size * 0.20f)
                    .clip(CircleShape)
                    .background(HuskyGold),
            )
        }

        if (showWordmark) {
            Text(
                text = "HUSKYMINGLE",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = (size.value * 0.20f).coerceAtLeast(14f).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
            )
        }
    }
}

@Composable
fun HuskyMingleLogoMark(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    background: Color = HuskyRed,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "HM",
            color = White,
            fontSize = (size.value * 0.40f).sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp,
        )
    }
}

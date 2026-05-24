package com.huskymingle.app.ui.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huskymingle.app.ui.components.HuskyMingleLogo
import com.huskymingle.app.ui.theme.HuskyRed
import com.huskymingle.app.ui.theme.HuskyRedDeep
import com.huskymingle.app.ui.theme.White
import kotlinx.coroutines.delay

/**
 * Animated splash held for ~1.4s while the auth state resolves.
 * Matches the iOS SplashView (logo + wordmark + tagline).
 */
@Composable
fun SplashScreen() {
    var shown by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.85f,
        label = "splashScale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        label = "splashAlpha",
    )

    LaunchedEffect(Unit) {
        shown = true
        delay(1_400)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(HuskyRed, HuskyRedDeep),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale),
        ) {
            HuskyMingleLogo(size = 120.dp, showWordmark = false)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "HUSKYMINGLE",
                color = White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your campus, one app",
                color = White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

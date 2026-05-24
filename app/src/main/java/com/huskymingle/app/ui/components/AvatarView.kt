package com.huskymingle.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.huskymingle.app.ui.theme.HuskyGold
import com.huskymingle.app.ui.theme.HuskyRed
import com.huskymingle.app.ui.theme.HuskyRedDeep
import com.huskymingle.app.ui.theme.White

/**
 * Initials avatar with a husky-red gradient — direct port of iOS AvatarView.
 */
@Composable
fun AvatarView(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    accent: Color = HuskyGold,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(HuskyRed, HuskyRedDeep),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initialsFor(name),
            color = accent,
            fontSize = (size.value * 0.40f).sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )
    }
}

/**
 * Remote avatar with initials fallback while loading or on error.
 */
@Composable
fun RemoteAvatarView(
    url: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    if (url.isNullOrBlank()) {
        AvatarView(name = name, modifier = modifier, size = size)
        return
    }
    Box(
        modifier = modifier.size(size).clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            loading = { AvatarView(name = name, size = size) },
            error = { AvatarView(name = name, size = size) },
        )
    }
}

private fun initialsFor(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "?"
    val parts = trimmed.split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].first().toString() + parts[1].first().toString()).uppercase()
    }
}

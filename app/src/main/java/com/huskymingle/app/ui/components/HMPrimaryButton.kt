package com.huskymingle.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyCoral
import com.huskymingle.app.ui.theme.HuskyRed
import com.huskymingle.app.ui.theme.HuskyRedDeep
import com.huskymingle.app.ui.theme.White
import com.huskymingle.app.ui.theme.hmBrandShadow

@Composable
fun HMPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .hmBrandShadow(radius = HMTheme.radius.lg)
            .clip(RoundedCornerShape(HMTheme.radius.lg))
            .background(
                if (enabled) Brush.linearGradient(listOf(HuskyRed, HuskyCoral))
                else Brush.linearGradient(listOf(HuskyRed.copy(alpha = 0.4f), HuskyCoral.copy(alpha = 0.4f)))
            )
            .clickable(enabled = enabled && !loading) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = White,
                strokeWidth = 2.dp,
                modifier = Modifier.height(20.dp),
            )
        } else {
            Text(
                text = label,
                color = White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun HMSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(HMTheme.radius.lg))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, HuskyRed.copy(alpha = if (enabled) 1f else 0.4f), RoundedCornerShape(HMTheme.radius.lg))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (enabled) HuskyRed else HuskyRed.copy(alpha = 0.4f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

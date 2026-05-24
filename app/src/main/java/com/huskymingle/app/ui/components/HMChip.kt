package com.huskymingle.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed

@Composable
fun HMChip(
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val bg = if (selected) HuskyRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) HuskyRed else MaterialTheme.colorScheme.onSurfaceVariant
    val border = if (selected) HuskyRed.copy(alpha = 0.40f) else Color.Transparent

    val clickable = if (onClick != null) Modifier.clickable { onClick() } else Modifier

    Text(
        text = label,
        color = fg,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(HMTheme.radius.pill))
            .background(bg)
            .border(BorderStroke(1.dp, border), RoundedCornerShape(HMTheme.radius.pill))
            .then(clickable)
            .padding(PaddingValues(horizontal = HMTheme.spacing.sm, vertical = 6.dp)),
    )
}

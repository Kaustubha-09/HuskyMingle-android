package com.huskymingle.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Identity-mode the user is operating in — one verified identity, four social contexts.
 * Mirrors HMMode on iOS.
 */
enum class HMMode(
    val label: String,
    val tagline: String,
    val icon: ImageVector,
) {
    STUDY("Study", "Study buddy", Icons.Outlined.Book),
    PROJECT("Project", "Project partner", Icons.Outlined.Lightbulb),
    FRIEND("Friend", "Friends & fun", Icons.Outlined.People),
    NETWORK("Network", "Networking", Icons.Outlined.Work);

    companion object {
        fun fromRaw(raw: String?): HMMode =
            values().firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: FRIEND
    }
}

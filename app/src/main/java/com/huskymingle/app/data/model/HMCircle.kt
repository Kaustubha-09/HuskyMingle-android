package com.huskymingle.app.data.model

import java.util.UUID

/**
 * Private group — local-only for now (no backend). Mirrors HMCircle on iOS.
 */
data class HMCircle(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val emoji: String = "👥",
    val memberHandles: List<String> = emptyList(),
    val createdAtEpochMs: Long = System.currentTimeMillis(),
)

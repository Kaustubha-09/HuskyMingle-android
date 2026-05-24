package com.huskymingle.app.data.model

import java.util.UUID

/**
 * Ephemeral story — JPEG on disk + lightweight metadata in DataStore.
 * Mirrors HMStory on iOS; entries are reaped after 24h.
 */
data class HMStory(
    val id: String = UUID.randomUUID().toString(),
    val imageFilename: String,
    val caption: String? = null,
    val authorName: String,
    val authorInitials: String,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
) {
    val expiresAtEpochMs: Long get() = createdAtEpochMs + STORY_TTL_MS

    val isExpired: Boolean get() = System.currentTimeMillis() >= expiresAtEpochMs

    companion object {
        const val STORY_TTL_MS: Long = 24L * 60L * 60L * 1000L
    }
}

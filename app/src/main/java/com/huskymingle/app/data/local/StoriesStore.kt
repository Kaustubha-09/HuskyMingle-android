package com.huskymingle.app.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huskymingle.app.data.model.HMStory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Singleton store for ephemeral stories. JPEGs live in `files/stories/`,
 * metadata is mirrored to DataStore as JSON. Expired stories (24h+) are
 * reaped on every load.
 */
class StoriesStore(
    private val context: Context,
    private val userPreferences: UserPreferences,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()
    private val type = object : TypeToken<List<HMStory>>() {}.type

    private val _stories = MutableStateFlow<List<HMStory>>(emptyList())
    val stories: StateFlow<List<HMStory>> = _stories.asStateFlow()

    private val storiesDir: File by lazy {
        File(context.filesDir, "stories").apply { if (!exists()) mkdirs() }
    }

    init {
        scope.launch { restoreFromDisk() }
    }

    private suspend fun restoreFromDisk() {
        val raw = userPreferences.storiesJson.firstOrNull()
        val parsed: List<HMStory> = try {
            if (raw.isNullOrBlank()) emptyList() else gson.fromJson(raw, type)
        } catch (_: Exception) {
            emptyList()
        }
        val live = parsed.filterNot { it.isExpired }
        val reaped = parsed - live.toSet()
        reaped.forEach { story ->
            runCatching { File(storiesDir, story.imageFilename).delete() }
        }
        _stories.value = live
        persist(live)
    }

    private suspend fun persist(stories: List<HMStory>) {
        userPreferences.setStoriesJson(gson.toJson(stories))
    }

    fun addStory(
        sourceUri: Uri,
        caption: String?,
        authorName: String,
        authorInitials: String,
    ) {
        scope.launch {
            val filename = "story_${System.currentTimeMillis()}.jpg"
            val outFile = File(storiesDir, filename)
            val bitmap = context.contentResolver.openInputStream(sourceUri)?.use { input ->
                BitmapFactory.decodeStream(input)
            } ?: return@launch
            FileOutputStream(outFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            }
            val story = HMStory(
                imageFilename = filename,
                caption = caption?.takeIf { it.isNotBlank() },
                authorName = authorName,
                authorInitials = authorInitials,
            )
            val next = listOf(story) + _stories.value
            _stories.value = next
            persist(next)
        }
    }

    fun fileFor(story: HMStory): File = File(storiesDir, story.imageFilename)

    fun byId(id: String): HMStory? = _stories.value.firstOrNull { it.id == id }
}

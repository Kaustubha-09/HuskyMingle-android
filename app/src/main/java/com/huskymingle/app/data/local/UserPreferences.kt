package com.huskymingle.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Lightweight, device-local prefs: biometric lock toggle, current mode,
 * enrolled NEU course IDs, plus serialized HMStory / HMCircle blobs.
 * Mirrors the iOS @AppStorage / UserDefaults usage.
 */
class UserPreferences(private val context: Context) {

    companion object {
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val CURRENT_MODE = stringPreferencesKey("current_mode")
        val ENROLLED_COURSE_IDS = stringSetPreferencesKey("enrolled_course_ids")
        val STORIES_JSON = stringPreferencesKey("stories_json_v1")
        val CIRCLES_JSON = stringPreferencesKey("circles_json_v1")
    }

    val biometricEnabled: Flow<Boolean> =
        context.userPrefsDataStore.data.map { it[BIOMETRIC_ENABLED] ?: false }

    val currentMode: Flow<String?> =
        context.userPrefsDataStore.data.map { it[CURRENT_MODE] }

    val enrolledCourseIds: Flow<Set<String>> =
        context.userPrefsDataStore.data.map { it[ENROLLED_COURSE_IDS] ?: emptySet() }

    val storiesJson: Flow<String?> =
        context.userPrefsDataStore.data.map { it[STORIES_JSON] }

    val circlesJson: Flow<String?> =
        context.userPrefsDataStore.data.map { it[CIRCLES_JSON] }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.userPrefsDataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setCurrentMode(modeName: String) {
        context.userPrefsDataStore.edit { it[CURRENT_MODE] = modeName }
    }

    suspend fun toggleEnrolledCourse(courseId: String) {
        context.userPrefsDataStore.edit { prefs ->
            val current = prefs[ENROLLED_COURSE_IDS] ?: emptySet()
            prefs[ENROLLED_COURSE_IDS] =
                if (courseId in current) current - courseId else current + courseId
        }
    }

    suspend fun setEnrolledCourseIds(ids: Set<String>) {
        context.userPrefsDataStore.edit { it[ENROLLED_COURSE_IDS] = ids }
    }

    suspend fun setStoriesJson(json: String) {
        context.userPrefsDataStore.edit { it[STORIES_JSON] = json }
    }

    suspend fun setCirclesJson(json: String) {
        context.userPrefsDataStore.edit { it[CIRCLES_JSON] = json }
    }

    suspend fun clearAll() {
        context.userPrefsDataStore.edit { it.clear() }
    }
}

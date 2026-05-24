package com.huskymingle.app.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huskymingle.app.data.model.Course

/**
 * Loads the bundled NEU course catalog from `assets/neu_courses.json`.
 * Search + department filtering happen client-side; enrollment is persisted
 * separately in [UserPreferences].
 */
class CourseCatalogService(private val context: Context) {

    private val gson = Gson()

    @Volatile
    private var cached: List<Course>? = null

    fun all(): List<Course> {
        cached?.let { return it }
        val raw = context.assets.open("neu_courses.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<RawCourse>>() {}.type
        val parsed: List<RawCourse> = try {
            gson.fromJson(raw, type)
        } catch (_: Exception) {
            emptyList()
        }
        val mapped = parsed.map { it.toCourse() }
        cached = mapped
        return mapped
    }

    fun byCode(code: String): Course? = all().firstOrNull { it.code.equals(code, ignoreCase = true) }

    fun departments(): List<String> = all().map { it.department }.distinct().sorted()

    fun search(
        query: String = "",
        department: String? = null,
    ): List<Course> = all().filter { c ->
        (department == null || c.department.equals(department, ignoreCase = true)) &&
            (query.isBlank() ||
                c.code.contains(query, ignoreCase = true) ||
                c.name.contains(query, ignoreCase = true) ||
                c.instructor.contains(query, ignoreCase = true))
    }

    private data class RawCourse(
        val code: String = "",
        val name: String = "",
        val department: String = "",
        val credits: Int = 0,
        val instructor: String = "",
        val description: String = "",
    ) {
        fun toCourse(): Course = Course(
            id = code,
            code = code,
            name = name,
            department = department,
            credits = credits,
            instructor = instructor,
            description = description,
        )
    }
}

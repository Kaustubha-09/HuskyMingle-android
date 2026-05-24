package com.huskymingle.app.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.huskymingle.app.data.model.HMCircle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CirclesStore(private val userPreferences: UserPreferences) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()
    private val type = object : TypeToken<List<HMCircle>>() {}.type

    private val _circles = MutableStateFlow<List<HMCircle>>(emptyList())
    val circles: StateFlow<List<HMCircle>> = _circles.asStateFlow()

    init {
        scope.launch { restore() }
    }

    private suspend fun restore() {
        val raw = userPreferences.circlesJson.firstOrNull()
        _circles.value = try {
            if (raw.isNullOrBlank()) emptyList() else gson.fromJson(raw, type)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun persist(list: List<HMCircle>) {
        userPreferences.setCirclesJson(gson.toJson(list))
    }

    fun add(circle: HMCircle) {
        scope.launch {
            val next = listOf(circle) + _circles.value
            _circles.value = next
            persist(next)
        }
    }

    fun update(updated: HMCircle) {
        scope.launch {
            val next = _circles.value.map { if (it.id == updated.id) updated else it }
            _circles.value = next
            persist(next)
        }
    }

    fun delete(id: String) {
        scope.launch {
            val next = _circles.value.filterNot { it.id == id }
            _circles.value = next
            persist(next)
        }
    }

    fun byId(id: String): HMCircle? = _circles.value.firstOrNull { it.id == id }
}

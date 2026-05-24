package com.huskymingle.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huskymingle.app.data.model.Comment
import com.huskymingle.app.data.model.CreateCommentRequest
import com.huskymingle.app.data.model.Post
import com.huskymingle.app.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PostDetailState {
    object Loading : PostDetailState()
    data class Error(val message: String) : PostDetailState()
    data class Success(
        val post: Post,
        val comments: List<Comment>,
    ) : PostDetailState()
}

class PostDetailViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private var postId: String? = null

    fun load(id: String) {
        postId = id
        _state.value = PostDetailState.Loading
        viewModelScope.launch {
            try {
                val post = api.getPost(id)
                val comments = runCatching { api.getComments(id) }.getOrDefault(emptyList())
                _state.value = PostDetailState.Success(post, comments)
            } catch (e: Exception) {
                _state.value = PostDetailState.Error(e.message ?: "Couldn't load post")
            }
        }
    }

    fun submitComment(content: String, parentId: String? = null) {
        val id = postId ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                val created = api.createComment(id, CreateCommentRequest(content, parentId))
                val current = _state.value as? PostDetailState.Success ?: return@launch
                val next = if (parentId == null) {
                    current.comments + created
                } else {
                    current.comments.map { c ->
                        if (c.id == parentId) c.copy(replies = c.replies + created) else c
                    }
                }
                _state.value = current.copy(comments = next)
            } catch (_: Exception) {
                // Surface to UI via error flow later; for now swallow.
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}

package com.huskymingle.app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huskymingle.app.data.model.Post
import com.huskymingle.app.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FeedState {
    object Loading : FeedState()
    data class Success(val posts: List<Post>, val nextCursor: String? = null) : FeedState()
    data class Error(val message: String) : FeedState()
}

class FeedViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _feedState = MutableStateFlow<FeedState>(FeedState.Loading)
    val feedState: StateFlow<FeedState> = _feedState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPosts = mutableListOf<Post>()
    private var nextCursor: String? = null
    private var isLoadingMore = false

    init {
        loadFeed()
    }

    fun loadFeed(refresh: Boolean = false) {
        if (refresh) {
            _isRefreshing.value = true
            currentPosts.clear()
            nextCursor = null
        } else {
            _feedState.value = FeedState.Loading
        }
        viewModelScope.launch {
            try {
                val posts = api.getFeed(cursor = null, limit = 20)
                currentPosts = posts.toMutableList()
                nextCursor = null
                _feedState.value = FeedState.Success(currentPosts.toList(), nextCursor)
            } catch (e: Exception) {
                _feedState.value = FeedState.Error(e.message ?: "Failed to load feed")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadMore() {
        if (isLoadingMore || nextCursor == null) return
        isLoadingMore = true
        viewModelScope.launch {
            try {
                val morePosts = api.getFeed(cursor = nextCursor, limit = 20)
                currentPosts.addAll(morePosts)
                _feedState.value = FeedState.Success(currentPosts.toList(), nextCursor)
            } catch (e: Exception) {
                // silently fail for pagination
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            try {
                if (post.isLiked) {
                    api.unlikePost(post.id)
                } else {
                    api.likePost(post.id)
                }
                val idx = currentPosts.indexOfFirst { it.id == post.id }
                if (idx >= 0) {
                    currentPosts[idx] = post.copy(
                        isLiked = !post.isLiked,
                        likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                    )
                    _feedState.value = FeedState.Success(currentPosts.toList(), nextCursor)
                }
            } catch (e: Exception) {
                // no-op
            }
        }
    }
}

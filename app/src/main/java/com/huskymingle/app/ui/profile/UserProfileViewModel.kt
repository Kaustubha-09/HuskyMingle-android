package com.huskymingle.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huskymingle.app.data.model.User
import com.huskymingle.app.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserProfileState {
    object Loading : UserProfileState()
    data class Error(val message: String) : UserProfileState()
    data class Success(val user: User, val isFollowing: Boolean) : UserProfileState()
}

class UserProfileViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow<UserProfileState>(UserProfileState.Loading)
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    fun load(username: String) {
        _state.value = UserProfileState.Loading
        viewModelScope.launch {
            try {
                val user = api.getUserProfile(username)
                _state.value = UserProfileState.Success(user = user, isFollowing = false)
            } catch (e: Exception) {
                _state.value = UserProfileState.Error(e.message ?: "Couldn't load profile")
            }
        }
    }

    fun toggleFollow() {
        val current = _state.value as? UserProfileState.Success ?: return
        val target = current.user
        val nowFollowing = !current.isFollowing
        _state.value = current.copy(
            isFollowing = nowFollowing,
            user = target.copy(
                followersCount = (target.followersCount + if (nowFollowing) 1 else -1).coerceAtLeast(0)
            ),
        )
        viewModelScope.launch {
            try {
                if (nowFollowing) api.followUser(target.id) else api.unfollowUser(target.id)
            } catch (_: Exception) {
                // Revert on failure
                _state.value = current
            }
        }
    }
}

sealed class UserListState {
    object Loading : UserListState()
    data class Error(val message: String) : UserListState()
    data class Success(val users: List<User>) : UserListState()
}

enum class UserListKind { FOLLOWERS, FOLLOWING }

class UserListViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state.asStateFlow()

    fun load(username: String, kind: UserListKind) {
        _state.value = UserListState.Loading
        viewModelScope.launch {
            try {
                val users = when (kind) {
                    UserListKind.FOLLOWERS -> api.getFollowers(username)
                    UserListKind.FOLLOWING -> api.getFollowing(username)
                }
                _state.value = UserListState.Success(users)
            } catch (e: Exception) {
                _state.value = UserListState.Error(e.message ?: "Couldn't load list")
            }
        }
    }
}

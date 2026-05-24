package com.huskymingle.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.data.model.*
import com.huskymingle.app.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()
    data class NeedsOnboarding(val user: User) : AuthState()
    data class LoggedIn(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authDataStore = (application as HuskyMingleApp).authDataStore
    private val api = RetrofitClient.apiService

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            val token = authDataStore.accessToken.firstOrNull()
            if (token == null) {
                _authState.value = AuthState.LoggedOut
                return@launch
            }
            // Seed volatile cache before making the first API call
            RetrofitClient.updateToken(token)
            try {
                val user = api.getMe()
                _authState.value = if (!user.onboardingCompleted) {
                    AuthState.NeedsOnboarding(user)
                } else {
                    AuthState.LoggedIn(user)
                }
            } catch (e: Exception) {
                RetrofitClient.updateToken(null)
                authDataStore.clearTokens()
                _authState.value = AuthState.LoggedOut
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = api.login(LoginRequest(email, password))
                authDataStore.saveTokens(response.accessToken, response.refreshToken, response.user.id)
                RetrofitClient.updateToken(response.accessToken)
                _authState.value = if (!response.user.onboardingCompleted) {
                    AuthState.NeedsOnboarding(response.user)
                } else {
                    AuthState.LoggedIn(response.user)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, username: String, firstName: String, lastName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                api.register(RegisterRequest(email, password, username, firstName, lastName))
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyEmail(email: String, code: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                api.verifyEmail(VerifyEmailRequest(email, code))
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Verification failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeOnboarding(interests: List<String>, skills: List<String>, languages: List<String>, major: String, graduationYear: Int?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val user = api.completeOnboarding(OnboardingRequest(interests, skills, languages, major, graduationYear))
                _authState.value = AuthState.LoggedIn(user)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Onboarding failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            RetrofitClient.updateToken(null)
            authDataStore.clearTokens()
            _authState.value = AuthState.LoggedOut
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

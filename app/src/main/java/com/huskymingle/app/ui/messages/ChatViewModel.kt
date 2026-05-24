package com.huskymingle.app.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huskymingle.app.data.model.Conversation
import com.huskymingle.app.data.model.Message
import com.huskymingle.app.data.model.SendMessageRequest
import com.huskymingle.app.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatState {
    object Loading : ChatState()
    data class Error(val message: String) : ChatState()
    data class Success(
        val conversation: Conversation?,
        val messages: List<Message>,
    ) : ChatState()
}

class ChatViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _state = MutableStateFlow<ChatState>(ChatState.Loading)
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private var conversationId: String? = null

    fun load(id: String) {
        conversationId = id
        _state.value = ChatState.Loading
        viewModelScope.launch {
            try {
                val convs = runCatching { api.getConversations() }.getOrDefault(emptyList())
                val match = convs.firstOrNull { it.id == id }
                val messages = api.getMessages(id)
                _state.value = ChatState.Success(conversation = match, messages = messages)
            } catch (e: Exception) {
                _state.value = ChatState.Error(e.message ?: "Couldn't load conversation")
            }
        }
    }

    fun send(content: String) {
        val id = conversationId ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            _isSending.value = true
            try {
                val created = api.sendMessage(id, SendMessageRequest(content))
                val current = _state.value as? ChatState.Success ?: return@launch
                _state.value = current.copy(messages = current.messages + created)
            } catch (_: Exception) {
                // ignored for now
            } finally {
                _isSending.value = false
            }
        }
    }
}

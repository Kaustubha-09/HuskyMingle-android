package com.huskymingle.app.ui.polls

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huskymingle.app.data.model.Poll
import com.huskymingle.app.data.model.PollVoteRequest
import com.huskymingle.app.data.network.RetrofitClient
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PollsViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _polls = MutableStateFlow<List<Poll>>(emptyList())
    val polls: StateFlow<List<Poll>> = _polls.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try { _polls.value = api.getPolls() }
            catch (e: Exception) { _error.value = e.message ?: "Failed to load polls" }
            finally { _isLoading.value = false }
        }
    }

    fun vote(pollId: String, optionId: String) {
        viewModelScope.launch {
            try {
                val updated = api.votePoll(pollId, PollVoteRequest(optionId))
                _polls.value = _polls.value.map { if (it.id == pollId) updated else it }
            } catch (e: Exception) { /* no-op */ }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollsScreen(viewModel: PollsViewModel = viewModel(), onMenuOpen: () -> Unit = {}) {
    val polls by viewModel.polls.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Polls", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onMenuOpen) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HuskyRed)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.load() },
                            colors = ButtonDefaults.buttonColors(containerColor = HuskyRed)) { Text("Retry") }
                    }
                }
                polls.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No polls available", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(polls, key = { it.id }) { poll ->
                        PollCard(poll = poll, onVote = { optionId -> viewModel.vote(poll.id, optionId) })
                    }
                }
            }
        }
    }
}

@Composable
fun PollCard(poll: Poll, onVote: (String) -> Unit) {
    val hasVoted = poll.userVotedOption != null
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(poll.question, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                "by @${poll.author.username} • ${poll.totalVotes} votes",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            poll.options.forEach { option ->
                val percentage = if (poll.totalVotes > 0) (option.votes.toFloat() / poll.totalVotes * 100).toInt() else 0
                val isSelected = poll.userVotedOption == option.id

                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(option.text, fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                        if (hasVoted) Text("$percentage%", fontSize = 13.sp, color = HuskyRed, fontWeight = FontWeight.Medium)
                    }
                    if (hasVoted) {
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { percentage / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = if (isSelected) HuskyRed else MaterialTheme.colorScheme.surfaceVariant,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    } else {
                        Spacer(Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { onVote(option.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) { Text(option.text, fontSize = 13.sp) }
                    }
                }
            }
        }
    }
}

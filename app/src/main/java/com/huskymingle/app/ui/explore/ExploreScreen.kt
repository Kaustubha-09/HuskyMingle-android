package com.huskymingle.app.ui.explore

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
import com.huskymingle.app.data.model.MatchUser
import com.huskymingle.app.data.network.RetrofitClient
import com.huskymingle.app.ui.feed.AvatarInitials
import com.huskymingle.app.ui.theme.HuskyGold
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ExploreViewModel : ViewModel() {
    private val api = RetrofitClient.apiService

    private val _matches = MutableStateFlow<List<MatchUser>>(emptyList())
    val matches: StateFlow<List<MatchUser>> = _matches.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val followingIds = mutableSetOf<String>()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _matches.value = api.getMatches()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load recommendations"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            try {
                if (userId in followingIds) {
                    api.unfollowUser(userId)
                    followingIds.remove(userId)
                } else {
                    api.followUser(userId)
                    followingIds.add(userId)
                }
                _matches.value = _matches.value.map { m ->
                    if (m.user.id == userId) m.copy(isFollowing = userId in followingIds) else m
                }
            } catch (e: Exception) {
                // no-op
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: ExploreViewModel = viewModel(), onMenuOpen: () -> Unit = {}) {
    val allMatches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = allMatches.filter { m ->
        val name = m.user.displayName.ifEmpty { m.user.username }
        searchQuery.isBlank() ||
                name.contains(searchQuery, ignoreCase = true) ||
                m.sharedInterests.any { it.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onMenuOpen) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name or interest…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = HuskyRed)
                    }
                }
                error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(error!!, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.load() },
                                colors = ButtonDefaults.buttonColors(containerColor = HuskyRed)) {
                                Text("Retry")
                            }
                        }
                    }
                }
                filtered.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No matches found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Recommended Connections",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        items(filtered, key = { it.user.id }) { match ->
                            MatchCard(match = match, onFollow = { viewModel.toggleFollow(match.user.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: MatchUser, onFollow: () -> Unit) {
    val name = match.user.displayName.ifEmpty { match.user.username }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AvatarInitials(name = name, size = 48.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(
                        "@${match.user.username} • ${match.user.major.ifEmpty { "NEU Student" }}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${(match.score * 100).roundToInt()}%",
                        fontWeight = FontWeight.Bold,
                        color = HuskyGold,
                        fontSize = 16.sp
                    )
                    Text("match", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (match.sharedInterests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    match.sharedInterests.take(3).forEach { interest ->
                        AssistChip(
                            onClick = {},
                            label = { Text(interest, fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                    if (match.sharedInterests.size > 3) {
                        Text(
                            "+${match.sharedInterests.size - 3} more",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onFollow,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (match.isFollowing) MaterialTheme.colorScheme.surfaceVariant else HuskyRed,
                    contentColor = if (match.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (match.isFollowing) "Following" else "Follow")
            }
        }
    }
}

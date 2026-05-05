package com.huskymingle.app.ui.search

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
import com.huskymingle.app.data.model.SearchResult
import com.huskymingle.app.data.network.RetrofitClient
import com.huskymingle.app.ui.feed.AvatarInitials
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _results = MutableStateFlow<SearchResult?>(null)
    val results: StateFlow<SearchResult?> = _results.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _results.value = null
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            _isLoading.value = true
            _error.value = null
            try { _results.value = api.search(query) }
            catch (e: Exception) { _error.value = e.message ?: "Search failed" }
            finally { _isLoading.value = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel(), onMenuOpen: () -> Unit = {}) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onMenuOpen) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.search(it)
                },
                placeholder = { Text("Search people, posts, events…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = ""; viewModel.search("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HuskyRed)
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }
                results == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Search for Huskies, posts, events, and communities",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    val r = results!!
                    val totalCount = r.users.size + r.posts.size + r.events.size + r.communities.size
                    if (totalCount == 0) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No results for \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            if (r.users.isNotEmpty()) {
                                item {
                                    SectionHeader("People (${r.users.size})")
                                }
                                items(r.users) { user ->
                                    val name = user.displayName.ifEmpty { user.username }
                                    ListItem(
                                        headlineContent = { Text(name, fontWeight = FontWeight.Medium) },
                                        supportingContent = { Text("@${user.username}") },
                                        leadingContent = { AvatarInitials(name = name, size = 40.dp) }
                                    )
                                    HorizontalDivider()
                                }
                            }
                            if (r.events.isNotEmpty()) {
                                item { SectionHeader("Events (${r.events.size})") }
                                items(r.events) { event ->
                                    ListItem(
                                        headlineContent = { Text(event.title, fontWeight = FontWeight.Medium) },
                                        supportingContent = { Text(event.location) },
                                        leadingContent = {
                                            Icon(Icons.Default.Event, contentDescription = null, tint = HuskyRed)
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                            if (r.communities.isNotEmpty()) {
                                item { SectionHeader("Communities (${r.communities.size})") }
                                items(r.communities) { community ->
                                    ListItem(
                                        headlineContent = { Text(community.name, fontWeight = FontWeight.Medium) },
                                        supportingContent = { Text("${community.membersCount} members") },
                                        leadingContent = {
                                            Icon(Icons.Default.Group, contentDescription = null, tint = HuskyRed)
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = HuskyRed,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

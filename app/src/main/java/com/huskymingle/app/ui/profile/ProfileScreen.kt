package com.huskymingle.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huskymingle.app.data.model.User
import com.huskymingle.app.data.network.RetrofitClient
import com.huskymingle.app.ui.auth.AuthViewModel
import com.huskymingle.app.ui.auth.AuthState
import com.huskymingle.app.ui.feed.AvatarInitials
import com.huskymingle.app.ui.theme.HuskyGold
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val api = RetrofitClient.apiService
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try { _user.value = api.getUserProfile(username) }
            catch (e: Exception) { _error.value = e.message ?: "Failed to load profile" }
            finally { _isLoading.value = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
    onMenuOpen: () -> Unit = {}
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser = when (val s = authState) {
        is AuthState.LoggedIn -> s.user
        else -> null
    }

    LaunchedEffect(currentUser?.username) {
        currentUser?.username?.let { profileViewModel.load(it) }
    }

    val profileUser by profileViewModel.user.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val error by profileViewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onMenuOpen) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = HuskyRed)
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HuskyRed)
                }
                error != null || profileUser == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error ?: "User not found", color = MaterialTheme.colorScheme.error)
                }
                else -> ProfileContent(user = profileUser!!)
            }
        }
    }
}

@Composable
fun ProfileContent(user: User) {
    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HuskyRed)
                    .padding(bottom = 48.dp)
                    .height(120.dp)
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-48).dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    AvatarInitials(
                        name = user.displayName.ifEmpty { user.username },
                        size = 80.dp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) { Text("Edit Profile", fontSize = 13.sp) }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    user.displayName.ifEmpty { user.username },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    "@${user.username}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )

                if (user.bio.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(user.bio, fontSize = 14.sp)
                }

                if (user.major.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.School, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Text(user.major, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (user.graduationYear != null) {
                            Text("• Class of ${user.graduationYear}", fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(label = "Followers", value = user.followersCount)
                    StatItem(label = "Following", value = user.followingCount)
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        if (user.interests.isNotEmpty()) {
            item {
                ProfileSection(
                    title = "Interests",
                    chips = user.interests,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)
                )
            }
        }
        if (user.skills.isNotEmpty()) {
            item {
                ProfileSection(
                    title = "Skills",
                    chips = user.skills,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)
                )
            }
        }
        if (user.languages.isNotEmpty()) {
            item {
                ProfileSection(
                    title = "Languages",
                    chips = user.languages,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSection(title: String, chips: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEach { chip ->
                AssistChip(onClick = {}, label = { Text(chip, fontSize = 12.sp) })
            }
        }
    }
}

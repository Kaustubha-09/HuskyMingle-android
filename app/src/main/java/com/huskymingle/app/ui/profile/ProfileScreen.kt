package com.huskymingle.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.data.model.User
import com.huskymingle.app.data.network.RetrofitClient
import com.huskymingle.app.ui.auth.AuthState
import com.huskymingle.app.ui.auth.AuthViewModel
import com.huskymingle.app.ui.components.AvatarView
import com.huskymingle.app.ui.components.HMChip
import com.huskymingle.app.ui.components.HMMode
import com.huskymingle.app.ui.components.ModeBadge
import com.huskymingle.app.ui.components.ModeSwitcher
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed
import com.huskymingle.app.ui.theme.HuskyRedDeep
import com.huskymingle.app.ui.theme.White
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
            try {
                _user.value = api.getUserProfile(username)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel = viewModel(),
    onMenuOpen: () -> Unit = {},
    onOpenCircles: () -> Unit = {},
    onOpenFollowers: (String) -> Unit = {},
    onOpenFollowing: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val app = remember { context.applicationContext as HuskyMingleApp }
    val scope = rememberCoroutineScope()

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
    val storedModeRaw by app.userPreferences.currentMode.collectAsState(initial = null)
    val mode = HMMode.fromRaw(storedModeRaw)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuOpen) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sign out",
                            tint = HuskyRed
                        )
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
                else -> ProfileContent(
                    user = profileUser!!,
                    mode = mode,
                    onModeChange = { next -> scope.launch { app.userPreferences.setCurrentMode(next.name) } },
                    onOpenFollowers = { onOpenFollowers(profileUser!!.username) },
                    onOpenFollowing = { onOpenFollowing(profileUser!!.username) },
                    onOpenCircles = onOpenCircles,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileContent(
    user: User,
    mode: HMMode,
    onModeChange: (HMMode) -> Unit,
    onOpenFollowers: () -> Unit,
    onOpenFollowing: () -> Unit,
    onOpenCircles: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.md),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        item {
            // Hero header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(HuskyRed, HuskyRedDeep)))
                    .padding(top = 24.dp, bottom = 56.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AvatarView(
                        name = user.displayName.ifEmpty { user.username },
                        size = 88.dp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = user.displayName.ifEmpty { user.username },
                        color = White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "@${user.username}",
                        color = White.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    ModeBadge(mode = mode)
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = HMTheme.spacing.md)) {
                if (user.bio.isNotBlank()) {
                    Text(user.bio, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(HMTheme.spacing.sm))
                }

                if (user.major.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = user.major,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (user.graduationYear != null) {
                            Text(
                                text = "· Class of ${user.graduationYear}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(Modifier.height(HMTheme.spacing.sm))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    StatItem(label = "Followers", value = user.followersCount, onClick = onOpenFollowers)
                    StatItem(label = "Following", value = user.followingCount, onClick = onOpenFollowing)
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = HMTheme.spacing.md)) {
                SectionLabel("Mode")
                Spacer(Modifier.height(6.dp))
                ModeSwitcher(selected = mode, onSelect = onModeChange)
            }
        }

        if (user.interests.isNotEmpty()) {
            item { ProfileChipSection(title = "Interests", chips = user.interests) }
        }
        if (user.skills.isNotEmpty()) {
            item { ProfileChipSection(title = "Skills", chips = user.skills) }
        }
        if (user.languages.isNotEmpty()) {
            item { ProfileChipSection(title = "Languages", chips = user.languages) }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = HMTheme.spacing.md)) {
                SectionLabel("More")
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(HMTheme.radius.lg))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onOpenCircles() }
                        .padding(HMTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HuskyRed.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center,
                    ) { Text("👥", fontSize = 22.sp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Circles",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            "Private groups",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: Int, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.clickable { onClick() },
    ) {
        Text(value.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileChipSection(title: String, chips: List<String>) {
    Column(modifier = Modifier.padding(horizontal = HMTheme.spacing.md)) {
        SectionLabel(title)
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            chips.forEach { chip -> HMChip(label = chip) }
        }
    }
}

// Kept for backwards compatibility — some screens import this name.
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

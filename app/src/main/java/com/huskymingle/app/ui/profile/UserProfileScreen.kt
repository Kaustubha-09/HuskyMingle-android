package com.huskymingle.app.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huskymingle.app.data.model.User
import com.huskymingle.app.ui.components.AvatarView
import com.huskymingle.app.ui.components.HMChip
import com.huskymingle.app.ui.components.HMPrimaryButton
import com.huskymingle.app.ui.components.HMSecondaryButton
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    username: String,
    onBack: () -> Unit,
    onOpenFollowers: (String) -> Unit = {},
    onOpenFollowing: (String) -> Unit = {},
    viewModel: UserProfileViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(username) { viewModel.load(username) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("@$username", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        }
    ) { padding ->
        when (val s = state) {
            UserProfileState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = HuskyRed) }
            is UserProfileState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UserProfileState.Success -> ProfileContent(
                user = s.user,
                isFollowing = s.isFollowing,
                modifier = Modifier.padding(padding),
                onToggleFollow = { viewModel.toggleFollow() },
                onOpenFollowers = { onOpenFollowers(s.user.username) },
                onOpenFollowing = { onOpenFollowing(s.user.username) },
            )
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isFollowing: Boolean,
    modifier: Modifier = Modifier,
    onToggleFollow: () -> Unit,
    onOpenFollowers: () -> Unit,
    onOpenFollowing: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(HMTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HMTheme.spacing.md),
        ) {
            AvatarView(name = user.displayName.ifEmpty { user.username }, size = 80.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.displayName.ifEmpty { user.username },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                if (user.major.isNotBlank()) {
                    Text(
                        text = user.major + (user.graduationYear?.let { " · '$it" }?.takeLast(4) ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (user.isVerified) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "✓ Verified Husky",
                        color = HuskyRed,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        if (user.bio.isNotBlank()) {
            Text(user.bio, style = MaterialTheme.typography.bodyMedium)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Stat(
                label = "Followers",
                value = user.followersCount,
                onClick = onOpenFollowers,
            )
            Stat(
                label = "Following",
                value = user.followingCount,
                onClick = onOpenFollowing,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm)) {
            if (isFollowing) {
                HMSecondaryButton(
                    label = "Following",
                    onClick = onToggleFollow,
                    modifier = Modifier.weight(1f),
                )
            } else {
                HMPrimaryButton(
                    label = "Follow",
                    onClick = onToggleFollow,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (user.interests.isNotEmpty()) {
            SectionTitle("Interests")
            ChipFlow(items = user.interests)
        }
        if (user.skills.isNotEmpty()) {
            SectionTitle("Skills")
            ChipFlow(items = user.skills)
        }
        if (user.languages.isNotEmpty()) {
            SectionTitle("Languages")
            ChipFlow(items = user.languages)
        }
    }
}

@Composable
private fun Stat(label: String, value: Int, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ChipFlow(items: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(end = HMTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { item -> HMChip(label = item, selected = false) }
    }
}

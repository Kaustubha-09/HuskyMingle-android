package com.huskymingle.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.security.BiometricService
import com.huskymingle.app.ui.auth.AuthViewModel
import com.huskymingle.app.ui.theme.HuskyRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(authViewModel: AuthViewModel, onMenuOpen: () -> Unit = {}) {
    val context = LocalContext.current
    val app = remember { context.applicationContext as HuskyMingleApp }
    val scope = rememberCoroutineScope()
    val biometricEnabled by app.userPreferences.biometricEnabled.collectAsState(initial = false)
    val biometricAvailable = remember { BiometricService.isAvailable(context) }

    var notifications by remember { mutableStateOf(true) }
    var privateAccount by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
            SettingsSectionTitle("Security")
            SettingsToggle(
                title = "Biometric lock",
                subtitle = if (biometricAvailable)
                    "Require fingerprint or face to open the app"
                else
                    "No biometrics enrolled on this device",
                icon = Icons.Outlined.Fingerprint,
                checked = biometricEnabled && biometricAvailable,
                enabled = biometricAvailable,
                onCheckedChange = { value ->
                    scope.launch { app.userPreferences.setBiometricEnabled(value) }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionTitle("Privacy")
            SettingsToggle(
                title = "Private account",
                subtitle = "Only followers can see your posts",
                icon = Icons.Default.Lock,
                checked = privateAccount,
                onCheckedChange = { privateAccount = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionTitle("Notifications")
            SettingsToggle(
                title = "Push notifications",
                subtitle = "Receive in-app notifications",
                icon = Icons.Default.Notifications,
                checked = notifications,
                onCheckedChange = { notifications = it }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            SettingsSectionTitle("Account")
            ListItem(
                headlineContent = { Text("Change password") },
                leadingContent = { Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
            )
            ListItem(
                headlineContent = { Text("Blocked users") },
                leadingContent = { Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            ListItem(
                headlineContent = { Text("Sign out", color = HuskyRed, fontWeight = FontWeight.SemiBold) },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = HuskyRed) },
                modifier = Modifier.clickable { authViewModel.logout() }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        title,
        fontWeight = FontWeight.SemiBold,
        color = HuskyRed,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsToggle(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle, fontSize = 12.sp) },
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(checkedThumbColor = HuskyRed, checkedTrackColor = HuskyRed.copy(alpha = 0.5f))
            )
        }
    )
}


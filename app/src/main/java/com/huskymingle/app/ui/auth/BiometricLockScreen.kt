package com.huskymingle.app.ui.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.huskymingle.app.security.BiometricService
import com.huskymingle.app.ui.components.HuskyMingleLogo
import com.huskymingle.app.ui.theme.HuskyRed
import com.huskymingle.app.ui.theme.HuskyRedDeep
import com.huskymingle.app.ui.theme.White

@Composable
fun BiometricLockScreen(
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context as? FragmentActivity }
    var lastError by remember { mutableStateOf<String?>(null) }

    fun launchPrompt() {
        val act = activity ?: return
        BiometricService.authenticate(
            activity = act,
            onSuccess = onSuccess,
            onFailure = { lastError = it },
        )
    }

    LaunchedEffect(Unit) {
        launchPrompt()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(HuskyRed, HuskyRedDeep))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp),
        ) {
            HuskyMingleLogo(size = 88.dp, showWordmark = false)
            Spacer(modifier = Modifier.height(24.dp))
            Icon(
                imageVector = Icons.Outlined.Fingerprint,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(96.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Unlock HuskyMingle",
                color = White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = lastError ?: "Use biometrics to continue",
                color = White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(onClick = { launchPrompt() }) {
                Text("Try again", color = White, fontWeight = FontWeight.SemiBold)
            }
            TextButton(onClick = onCancel) {
                Text("Sign out instead", color = White.copy(alpha = 0.7f))
            }
        }
    }
}

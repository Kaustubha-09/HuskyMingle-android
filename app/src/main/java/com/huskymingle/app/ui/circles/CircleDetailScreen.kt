package com.huskymingle.app.ui.circles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.ui.components.AvatarView
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleDetailScreen(circleId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val store = remember { (context.applicationContext as HuskyMingleApp).circlesStore }
    val circles by store.circles.collectAsState()
    val circle = circles.firstOrNull { it.id == circleId }
    var newHandle by remember { mutableStateOf("") }

    if (circle == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Circle not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${circle.emoji} ${circle.name}", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        store.delete(circle.id)
                        onBack()
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete circle", tint = HuskyRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = HMTheme.spacing.md, vertical = HMTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
        ) {
            Text(
                text = "Members",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.xs),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(circle.memberHandles) { handle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(HMTheme.radius.md))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = HMTheme.spacing.sm, vertical = HMTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AvatarView(name = handle, size = 32.dp)
                        Text(
                            text = "@$handle",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        IconButton(
                            onClick = {
                                store.update(circle.copy(memberHandles = circle.memberHandles - handle))
                            }
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove", tint = HuskyRed)
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = newHandle,
                    onValueChange = { newHandle = it.removePrefix("@") },
                    placeholder = { Text("Add @username") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        val handle = newHandle.trim().removePrefix("@")
                        if (handle.isNotEmpty()) {
                            store.update(circle.copy(memberHandles = (circle.memberHandles + handle).distinct()))
                            newHandle = ""
                        }
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add", tint = HuskyRed)
                }
            }
        }
    }
}

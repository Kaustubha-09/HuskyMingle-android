package com.huskymingle.app.ui.circles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.data.model.HMCircle
import com.huskymingle.app.ui.components.HMPrimaryButton
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed

private val emojiChoices = listOf("👥", "📚", "💻", "🎮", "🍕", "🐾", "🎓", "🏀", "🎬", "🎵", "✈️", "🧪")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCircleScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    val store = remember { (context.applicationContext as HuskyMingleApp).circlesStore }
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(emojiChoices.first()) }
    var membersDraft by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New circle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
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
                .padding(HMTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(HMTheme.spacing.md),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Circle name") },
                placeholder = { Text("e.g. CS roommates") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Emoji", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                items(emojiChoices) { e ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (e == emoji) HuskyRed.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable { emoji = e },
                        contentAlignment = Alignment.Center,
                    ) { Text(e, fontSize = 28.sp) }
                }
            }

            OutlinedTextField(
                value = membersDraft,
                onValueChange = { membersDraft = it },
                label = { Text("Members (comma-separated @handles)") },
                placeholder = { Text("alice, bob, charlie") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
            )

            Box(modifier = Modifier.weight(1f))

            HMPrimaryButton(
                label = "Create circle",
                enabled = name.isNotBlank(),
                onClick = {
                    val handles = membersDraft.split(',', ' ', '\n')
                        .map { it.trim().removePrefix("@") }
                        .filter { it.isNotEmpty() }
                        .distinct()
                    store.add(
                        HMCircle(
                            name = name.trim(),
                            emoji = emoji,
                            memberHandles = handles,
                        )
                    )
                    onClose()
                },
            )
        }
    }
}

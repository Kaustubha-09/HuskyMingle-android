package com.huskymingle.app.ui.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.ui.components.AvatarView
import kotlinx.coroutines.delay

private const val AUTO_ADVANCE_MS = 5_000L

@Composable
fun StoryViewerScreen(
    initialStoryId: String,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val store = remember { (context.applicationContext as HuskyMingleApp).storiesStore }
    val stories by store.stories.collectAsState()

    var index by remember {
        mutableStateOf(stories.indexOfFirst { it.id == initialStoryId }.coerceAtLeast(0))
    }

    LaunchedEffect(stories) {
        if (stories.isEmpty()) onClose()
    }

    if (stories.isEmpty()) return
    val story = stories.getOrNull(index) ?: return
    val file = remember(story.id) { store.fileFor(story) }

    LaunchedEffect(story.id) {
        delay(AUTO_ADVANCE_MS)
        if (index < stories.lastIndex) index += 1 else onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(story.id) {
                detectTapGestures(
                    onTap = { offset ->
                        val widthPx = size.width
                        if (offset.x < widthPx / 3f) {
                            if (index > 0) index -= 1 else onClose()
                        } else if (offset.x > widthPx * 2 / 3f) {
                            if (index < stories.lastIndex) index += 1 else onClose()
                        } else {
                            onClose()
                        }
                    },
                )
            },
    ) {
        AsyncImage(
            model = file,
            contentDescription = story.caption,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )

        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
        ) {
            // Progress segments
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp),
            ) {
                stories.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                color = if (i <= index) Color.White else Color.White.copy(alpha = 0.3f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp),
                            ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarView(name = story.authorName, size = 36.dp)
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(
                    text = story.authorName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Caption
        story.caption?.let { cap ->
            Text(
                text = cap,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(PaddingValues(horizontal = 24.dp, vertical = 48.dp)),
            )
        }
    }
}

package com.huskymingle.app.ui.stories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.huskymingle.app.HuskyMingleApp
import com.huskymingle.app.data.model.HMStory
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyGold
import com.huskymingle.app.ui.theme.HuskyRed
import com.huskymingle.app.ui.theme.HuskyRedDeep
import com.huskymingle.app.ui.theme.White

@Composable
fun StoriesRingView(
    onCreateStory: () -> Unit,
    onOpenStory: (HMStory) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val store = remember { (context.applicationContext as HuskyMingleApp).storiesStore }
    val stories by store.stories.collectAsState()

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = HMTheme.spacing.md, vertical = HMTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
    ) {
        item { YourStoryBubble(onClick = onCreateStory) }
        items(stories, key = { it.id }) { story ->
            StoryBubble(story = story, onClick = { onOpenStory(story) })
        }
    }
}

@Composable
private fun YourStoryBubble(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, HuskyRed, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add story",
                tint = HuskyRed,
            )
        }
        Text(
            text = "Your story",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun StoryBubble(story: HMStory, onClick: () -> Unit) {
    val context = LocalContext.current
    val store = remember { (context.applicationContext as HuskyMingleApp).storiesStore }
    val file = remember(story.id) { store.fileFor(story) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(HuskyRed, HuskyGold, HuskyRedDeep))
                )
                .padding(3.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = file,
                    contentDescription = story.authorName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape),
                )
            }
        }
        Text(
            text = story.authorName.substringBefore(' '),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

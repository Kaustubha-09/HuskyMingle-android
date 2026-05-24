package com.huskymingle.app.ui.feed

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huskymingle.app.data.model.Comment
import com.huskymingle.app.data.model.Post
import com.huskymingle.app.ui.components.AvatarView
import com.huskymingle.app.ui.theme.HMTheme
import com.huskymingle.app.ui.theme.HuskyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onBack: () -> Unit,
    onOpenAuthor: (String) -> Unit = {},
    viewModel: PostDetailViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    var replyTo by remember { mutableStateOf<Comment?>(null) }
    var draft by remember { mutableStateOf("") }

    LaunchedEffect(postId) { viewModel.load(postId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
            )
        },
        bottomBar = {
            CommentInputBar(
                draft = draft,
                replyingTo = replyTo,
                isSubmitting = isSubmitting,
                onChange = { draft = it },
                onCancelReply = { replyTo = null },
                onSubmit = {
                    val text = draft
                    if (text.isBlank()) return@CommentInputBar
                    viewModel.submitComment(text, replyTo?.id)
                    draft = ""
                    replyTo = null
                },
            )
        },
    ) { padding ->
        when (val s = state) {
            PostDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = HuskyRed)
                }
            }
            is PostDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is PostDetailState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    item { PostHeader(post = s.post, onAuthorTap = { onOpenAuthor(s.post.author.username) }) }
                    item { HorizontalDivider() }
                    item {
                        Text(
                            text = "Comments (${s.comments.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(HMTheme.spacing.md),
                        )
                    }
                    items(s.comments, key = { it.id }) { comment ->
                        CommentRow(
                            comment = comment,
                            depth = 0,
                            onReply = { replyTo = it },
                            onTapAuthor = { onOpenAuthor(it) },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PostHeader(post: Post, onAuthorTap: () -> Unit) {
    Column(modifier = Modifier.padding(HMTheme.spacing.md)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
            modifier = Modifier.clickable { onAuthorTap() },
        ) {
            AvatarView(
                name = post.author.displayName.ifEmpty { post.author.username },
                size = 44.dp,
            )
            Column {
                Text(
                    text = post.author.displayName.ifEmpty { post.author.username },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "@${post.author.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(HMTheme.spacing.sm))
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyLarge,
        )
        if (post.hashtags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(HMTheme.spacing.xs))
            Text(
                text = post.hashtags.joinToString(" ") { "#$it" },
                color = HuskyRed,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.height(HMTheme.spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "${post.likesCount} likes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${post.commentsCount} comments",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CommentRow(
    comment: Comment,
    depth: Int,
    onReply: (Comment) -> Unit,
    onTapAuthor: (String) -> Unit,
) {
    val indent = (depth * 24).dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = HMTheme.spacing.md + indent, end = HMTheme.spacing.md, top = HMTheme.spacing.sm, bottom = HMTheme.spacing.sm),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(HMTheme.spacing.sm),
        ) {
            AvatarView(
                name = comment.author.displayName.ifEmpty { comment.author.username },
                size = 32.dp,
                modifier = Modifier.clickable { onTapAuthor(comment.author.username) },
            )
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(HMTheme.radius.md))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Column {
                        Text(
                            text = comment.author.displayName.ifEmpty { comment.author.username },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = comment.content,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                ) {
                    Text(
                        text = "Reply",
                        style = MaterialTheme.typography.labelSmall,
                        color = HuskyRed,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onReply(comment) },
                    )
                    if (comment.likesCount > 0) {
                        Text(
                            text = "${comment.likesCount} likes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        comment.replies.forEach { reply ->
            CommentRow(
                comment = reply,
                depth = depth + 1,
                onReply = onReply,
                onTapAuthor = onTapAuthor,
            )
        }
    }
}

@Composable
private fun CommentInputBar(
    draft: String,
    replyingTo: Comment?,
    isSubmitting: Boolean,
    onChange: (String) -> Unit,
    onCancelReply: () -> Unit,
    onSubmit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(HMTheme.spacing.sm),
    ) {
        replyingTo?.let { reply ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(HuskyRed.copy(alpha = 0.08f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = null,
                    tint = HuskyRed,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Replying to @${reply.author.username}",
                    style = MaterialTheme.typography.labelMedium,
                    color = HuskyRed,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onCancelReply, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = HuskyRed)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HMTheme.spacing.xs),
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = onChange,
                placeholder = { Text("Add a comment") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default,
                keyboardActions = KeyboardActions(onSend = { onSubmit() }),
                maxLines = 4,
            )
            IconButton(
                onClick = onSubmit,
                enabled = draft.isNotBlank() && !isSubmitting,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (draft.isNotBlank()) HuskyRed else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

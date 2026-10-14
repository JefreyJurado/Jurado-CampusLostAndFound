package com.example.campuslostandfound.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.data.ChatMessage
import com.example.campuslostandfound.data.SampleData
import com.example.campuslostandfound.ui.components.EmptyState
import com.example.campuslostandfound.ui.components.FormField
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Conversation — stateful entry point. Owns the text being typed and passes it,
 * with the [messages] for this item, to the stateless [ConversationContent].
 *
 * State:
 *  - draft → text in the input field; also decides whether Send is enabled.
 *    rememberSaveable keeps a half-typed message through rotation.
 *
 * The messages themselves are app-level state, so they are still there when the user leaves and comes back.
 */
@Composable
fun ConversationScreen(
    messages: List<ChatMessage>,
    onSendMessage: (text: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var draft by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Whenever a message is added, scroll so the newest one is visible.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    ConversationContent(
        modifier = modifier,
        messages = messages,
        draft = draft,
        listState = listState,
        onDraftChange = { draft = it },
        onSendClick = {
            // A second tap can land before Send redraws as disabled; the check stops an empty message.
            if (draft.isNotBlank()) {
                onSendMessage(draft)
                draft = ""
            }
        },
        onBackClick = onBackClick,
    )
}

/**
 * Conversation — stateless layout.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: ScreenHeader("Conversation", back arrow)
 *  ├─ content: EmptyState  (when messages is empty)
 *  │           LazyColumn of MessageBubble  (otherwise)
 *  └─ bottomBar: MessageInputBar (text field + Send)
 */
@Composable
fun ConversationContent(
    messages: List<ChatMessage>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Conversation", onBackClick = onBackClick) },
        bottomBar = {
            MessageInputBar(
                draft = draft,
                onDraftChange = onDraftChange,
                onSendClick = onSendClick,
            )
        },
    ) { innerPadding ->
        if (messages.isEmpty()) {
            EmptyState(
                message = "No messages yet.\nSay hi to start the conversation.",
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = listState,
                contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.ScreenPadding),
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

/**
 * One chat message. Received messages sit on the left in gray,
 * sent messages sit on the right in blue.
 */
@Composable
fun MessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 250.dp),
            shape = MaterialTheme.shapes.small,
            color = if (message.isFromMe) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = Dimens.SpacingLarge, vertical = 20.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/**
 * Text field and Send button pinned to the bottom of the screen.
 * Send is only enabled while [draft] contains text.
 */
@Composable
private fun MessageInputBar(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(Dimens.ScreenPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FormField(
            label = "Type a message",
            value = draft,
            onValueChange = onDraftChange,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
        GrayButton(
            text = "Send",
            enabled = draft.isNotBlank(),
            onClick = onSendClick,
            modifier = Modifier.width(80.dp),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConversationScreenPreview() {
    CampusLostAndFoundTheme {
        ConversationScreen(
            messages = SampleData.messages,
            onSendMessage = {},
            onBackClick = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Empty conversation")
@Composable
private fun ConversationEmptyPreview() {
    CampusLostAndFoundTheme {
        ConversationContent(
            messages = emptyList(),
            draft = "",
            onDraftChange = {},
            onSendClick = {},
            onBackClick = {},
        )
    }
}

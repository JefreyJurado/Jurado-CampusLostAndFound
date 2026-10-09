package com.example.campuslostandfound.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.data.ChatMessage
import com.example.campuslostandfound.data.SampleData
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Conversation — stateful entry point. Owns the message list and the text being typed,
 * and passes them to the stateless [ConversationContent].
 *
 * State:
 *  - messages → observable list; adding to it recomposes the LazyColumn
 *  - draft    → text in the input field; also decides whether Send is enabled
 */
@Composable
fun ConversationScreen(
    initialMessages: List<ChatMessage>,
    modifier: Modifier = Modifier,
) {
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(initialMessages) } }
    var draft by remember { mutableStateOf("") }
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
            messages.add(ChatMessage(text = draft.trim(), isFromMe = true))
            draft = ""
        },
    )
}

/**
 * Conversation — stateless layout.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: ScreenHeader("Conversation", back arrow)
 *  ├─ content: EmptyConversation  (when messages is empty)
 *  │           LazyColumn of MessageBubble  (otherwise)
 *  └─ bottomBar: MessageInputBar (text field + Send)
 */
@Composable
fun ConversationContent(
    messages: List<ChatMessage>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Conversation", showBackArrow = true) },
        bottomBar = {
            MessageInputBar(
                draft = draft,
                onDraftChange = onDraftChange,
                onSendClick = onSendClick,
            )
        },
    ) { innerPadding ->
        if (messages.isEmpty()) {
            EmptyConversation(modifier = Modifier.padding(innerPadding))
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
            shape = RoundedCornerShape(Dimens.CornerRadius),
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
 * Shown in place of the message list when no messages have been sent yet.
 */
@Composable
private fun EmptyConversation(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No messages yet.\nSay hi to start the conversation.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
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
        OutlinedTextField(
            value = draft,
            onValueChange = onDraftChange,
            modifier = Modifier
                .weight(1f)
                .height(Dimens.FieldHeight),
            placeholder = { Text(text = "Type a message", style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            shape = RoundedCornerShape(Dimens.CornerRadius),
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

// Interactive: use the preview's "Start Interactive Mode" button to type and send messages.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConversationScreenPreview() {
    CampusLostAndFoundTheme {
        ConversationScreen(initialMessages = SampleData.conversation)
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
        )
    }
}

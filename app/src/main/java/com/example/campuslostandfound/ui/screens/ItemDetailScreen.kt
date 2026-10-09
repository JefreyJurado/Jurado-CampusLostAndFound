package com.example.campuslostandfound.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.data.ItemStatus
import com.example.campuslostandfound.data.LostItem
import com.example.campuslostandfound.data.SampleData
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ImagePlaceholder
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.components.StatusBadge
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Item Detail — stateful entry point.
 *
 * State:
 *  - isSaved → whether the user bookmarked this item; drives the header icon and the "saved" note.
 */
@Composable
fun ItemDetailScreen(
    item: LostItem,
    modifier: Modifier = Modifier,
) {
    var isSaved by remember { mutableStateOf(false) }

    ItemDetailContent(
        modifier = modifier,
        item = item,
        isSaved = isSaved,
        onSaveClick = { isSaved = !isSaved },
    )
}

/**
 * Item Detail — stateless layout.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: ScreenHeader("Item Detail", back arrow, SaveButton)
 *  └─ content: Column (scrollable for long descriptions)
 *       ├─ ImagePlaceholder (large photo)
 *       ├─ ItemTitleSection (name, badge, "saved" note when isSaved)
 *       ├─ ItemInfoSection (Location, Posted, Description)
 *       └─ GrayButton("Message finder" / "Message owner")
 */
@Composable
fun ItemDetailContent(
    item: LostItem,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // A found item was posted by the finder; a lost item was posted by the owner.
    val messageLabel = when (item.status) {
        ItemStatus.FOUND -> "Message finder"
        ItemStatus.LOST -> "Message owner"
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ScreenHeader(title = "Item Detail", showBackArrow = true) {
                SaveButton(isSaved = isSaved, onClick = onSaveClick)
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding),
        ) {
            ImagePlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.DetailImageHeight),
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingLarge))
            ItemTitleSection(item = item, isSaved = isSaved)
            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
            ItemInfoSection(item = item)
            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
            GrayButton(text = messageLabel, modifier = Modifier.fillMaxWidth())
        }
    }
}

/**
 * Heart icon that is outlined when not saved and filled when saved.
 */
@Composable
private fun SaveButton(
    isSaved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isSaved) "Remove from saved" else "Save item",
        )
    }
}

@Composable
private fun ItemTitleSection(
    item: LostItem,
    isSaved: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        StatusBadge(
            status = item.status,
            width = 96.dp,
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        if (isSaved) {
            Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
            Text(
                text = "Saved to your list",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun ItemInfoSection(item: LostItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        DetailLine(label = "Location", value = item.location)
        DetailLine(label = "Posted", value = item.postedAt)
        DetailLine(label = "Description", value = item.description)
    }
}

/**
 * A "Label: value" line where only the label is bold.
 */
@Composable
fun DetailLine(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("$label: ") }
            append(value)
        },
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
    )
}

// Interactive: use the preview's "Start Interactive Mode" button to toggle the save icon.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemDetailScreenPreview() {
    CampusLostAndFoundTheme {
        ItemDetailScreen(item = SampleData.detailItem)
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Saved, lost item")
@Composable
private fun ItemDetailSavedLostPreview() {
    CampusLostAndFoundTheme {
        ItemDetailContent(
            item = SampleData.detailItem.copy(status = ItemStatus.LOST),
            isSaved = true,
            onSaveClick = {},
        )
    }
}

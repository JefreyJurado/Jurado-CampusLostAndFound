package com.example.campuslostandfound.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.data.LostItem
import com.example.campuslostandfound.data.SampleData
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ImagePlaceholder
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.components.StatusBadge
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens

/**
 * Item Detail — full information about one item so the user can confirm a match.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: ScreenHeader("Item Detail", back arrow)
 *  └─ content: Column (scrollable for long descriptions)
 *       ├─ ImagePlaceholder (large photo)
 *       ├─ Name + StatusBadge
 *       ├─ DetailLine × 3 (Location, Posted, Description)
 *       └─ GrayButton("Message finder")
 */
@Composable
fun ItemDetailScreen(
    item: LostItem,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Item Detail", showBackArrow = true) },
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
            ItemTitleSection(item = item)
            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
            ItemInfoSection(item = item)
            Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
            GrayButton(text = "Message finder", modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ItemTitleSection(item: LostItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        StatusBadge(
            status = item.status,
            width = 96.dp,
            textStyle = MaterialTheme.typography.bodyMedium,
        )
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ItemDetailScreenPreview() {
    CampusLostAndFoundTheme {
        ItemDetailScreen(item = SampleData.detailItem)
    }
}

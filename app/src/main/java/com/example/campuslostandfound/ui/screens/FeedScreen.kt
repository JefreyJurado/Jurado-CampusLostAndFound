package com.example.campuslostandfound.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * Feed — the launch screen. A scrollable list of recently reported items
 * with a "Report Item" action pinned to the bottom-right.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: ScreenHeader("Feed")
 *  ├─ content: LazyColumn of FeedItemRow
 *  └─ bottomBar: ReportItemBar
 */
@Composable
fun FeedScreen(
    items: List<LostItem>,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { ScreenHeader(title = "Feed") },
        bottomBar = { ReportItemBar() },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXLarge),
        ) {
            items(items) { item ->
                FeedItemRow(item = item)
            }
        }
    }
}

/**
 * One feed entry: photo on the left, name / location / status stacked on the right.
 */
@Composable
fun FeedItemRow(
    item: LostItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ImagePlaceholder(modifier = Modifier.size(Dimens.FeedImageWidth, Dimens.FeedImageHeight))
        Spacer(modifier = Modifier.width(Dimens.ScreenPadding))
        Column {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.location, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            StatusBadge(status = item.status)
        }
    }
}

/**
 * Bottom area holding the "Report Item" button, aligned to the end like the prototype.
 */
@Composable
private fun ReportItemBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(Dimens.ScreenPadding),
        contentAlignment = Alignment.CenterEnd,
    ) {
        GrayButton(text = "Report Item")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FeedScreenPreview() {
    CampusLostAndFoundTheme {
        FeedScreen(items = SampleData.feedItems)
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedItemRowPreview() {
    CampusLostAndFoundTheme {
        FeedItemRow(item = SampleData.feedItems.first(), modifier = Modifier.padding(Dimens.ScreenPadding))
    }
}

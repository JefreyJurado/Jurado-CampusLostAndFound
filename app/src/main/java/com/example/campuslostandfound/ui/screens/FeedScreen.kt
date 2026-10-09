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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
import com.example.campuslostandfound.ui.theme.SelectedGray

/**
 * Feed — stateful entry point and the app's launch screen.
 *
 * State:
 *  - selectedFilter → null means "All", otherwise only items with that status are shown.
 *    The visible list is derived from it, so there is no second copy of the items in state.
 */
@Composable
fun FeedScreen(
    items: List<LostItem>,
    modifier: Modifier = Modifier,
) {
    var selectedFilter by remember { mutableStateOf<ItemStatus?>(null) }

    val visibleItems = if (selectedFilter == null) {
        items
    } else {
        items.filter { it.status == selectedFilter }
    }

    FeedContent(
        modifier = modifier,
        items = visibleItems,
        selectedFilter = selectedFilter,
        onFilterSelect = { selectedFilter = it },
    )
}

/**
 * Feed — stateless layout.
 *
 * Hierarchy:
 * Scaffold
 *  ├─ topBar: Column
 *  │    ├─ ScreenHeader("Feed")
 *  │    └─ FeedFilterRow (All | Lost | Found)
 *  ├─ content: EmptyFeed  (when items is empty)
 *  │           LazyColumn of FeedItemRow  (otherwise)
 *  └─ bottomBar: ReportItemBar
 */
@Composable
fun FeedContent(
    items: List<LostItem>,
    selectedFilter: ItemStatus?,
    onFilterSelect: (ItemStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                ScreenHeader(title = "Feed")
                FeedFilterRow(selected = selectedFilter, onSelect = onFilterSelect)
            }
        },
        bottomBar = { ReportItemBar() },
    ) { innerPadding ->
        if (items.isEmpty()) {
            EmptyFeed(
                selectedFilter = selectedFilter,
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = Dimens.ScreenPadding,
                    end = Dimens.ScreenPadding,
                    top = Dimens.SpacingLarge,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXLarge),
            ) {
                items(items) { item ->
                    FeedItemRow(item = item)
                }
            }
        }
    }
}

/**
 * Chips for filtering the feed. Only one chip is selected at a time because
 * the parent holds a single [selected] value.
 */
@Composable
fun FeedFilterRow(
    selected: ItemStatus?,
    onSelect: (ItemStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    // null represents the "All" option.
    val options = listOf<ItemStatus?>(null) + ItemStatus.entries

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(text = option?.label ?: "All") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SelectedGray),
            )
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
 * Shown in place of the list when there are no items for the current filter.
 */
@Composable
private fun EmptyFeed(
    selectedFilter: ItemStatus?,
    modifier: Modifier = Modifier,
) {
    val message = if (selectedFilter == null) {
        "No items reported yet."
    } else {
        "No ${selectedFilter.label.lowercase()} items reported yet."
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
        )
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

// Interactive: use the preview's "Start Interactive Mode" button to tap the filter chips.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FeedScreenPreview() {
    CampusLostAndFoundTheme {
        FeedScreen(items = SampleData.feedItems)
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Empty feed")
@Composable
private fun FeedEmptyPreview() {
    CampusLostAndFoundTheme {
        FeedScreen(items = emptyList())
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedItemRowPreview() {
    CampusLostAndFoundTheme {
        FeedItemRow(item = SampleData.feedItems.first(), modifier = Modifier.padding(Dimens.ScreenPadding))
    }
}

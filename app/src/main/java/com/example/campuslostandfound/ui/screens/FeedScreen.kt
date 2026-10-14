package com.example.campuslostandfound.ui.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuslostandfound.data.ItemStatus
import com.example.campuslostandfound.data.LostItem
import com.example.campuslostandfound.data.SampleData
import com.example.campuslostandfound.ui.components.EmptyState
import com.example.campuslostandfound.ui.components.GrayButton
import com.example.campuslostandfound.ui.components.ItemPhoto
import com.example.campuslostandfound.ui.components.ReportPostedBanner
import com.example.campuslostandfound.ui.components.ScreenHeader
import com.example.campuslostandfound.ui.components.StatusBadge
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme
import com.example.campuslostandfound.ui.theme.Dimens
import com.example.campuslostandfound.ui.theme.SelectedGray
import kotlinx.coroutines.delay

/**
 * Feed — stateful entry point and the app's launch screen.
 *
 * State:
 *  - selectedFilter → null means "All", otherwise only items with that status are shown.
 *    The visible list is derived from it, so there is no second copy of the items in state.
 *    rememberSaveable keeps the filter when the user returns from another screen or rotates.
 *
 * [showPostedBanner] is raised by the app after a report is submitted; the banner hides itself
 * two seconds later by calling [onPostedBannerTimeout].
 */
@Composable
fun FeedScreen(
    items: List<LostItem>,
    showPostedBanner: Boolean,
    onPostedBannerTimeout: () -> Unit,
    onItemClick: (itemId: Int) -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedFilter by rememberSaveable { mutableStateOf<ItemStatus?>(null) }

    val visibleItems = if (selectedFilter == null) {
        items
    } else {
        items.filter { it.status == selectedFilter }
    }

    // A timer only: it hides the banner, it never navigates. `finally` also clears the banner
    // if the user leaves the Feed early, so it does not reappear when they come back.
    LaunchedEffect(showPostedBanner) {
        if (showPostedBanner) {
            try {
                delay(2_000)
            } finally {
                onPostedBannerTimeout()
            }
        }
    }

    FeedContent(
        modifier = modifier,
        items = visibleItems,
        selectedFilter = selectedFilter,
        showPostedBanner = showPostedBanner,
        onFilterSelect = { selectedFilter = it },
        onItemClick = onItemClick,
        onReportClick = onReportClick,
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
 *  ├─ content: Box
 *  │    ├─ EmptyFeed  (when items is empty)
 *  │    │  LazyColumn of FeedItemRow  (otherwise)
 *  │    └─ ReportPostedBanner (only while showPostedBanner is true)
 *  └─ bottomBar: ReportItemBar
 */
@Composable
fun FeedContent(
    items: List<LostItem>,
    selectedFilter: ItemStatus?,
    showPostedBanner: Boolean,
    onFilterSelect: (ItemStatus?) -> Unit,
    onItemClick: (itemId: Int) -> Unit,
    onReportClick: () -> Unit,
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
        bottomBar = { ReportItemBar(onReportClick = onReportClick) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (items.isEmpty()) {
                EmptyFeed(selectedFilter = selectedFilter)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimens.ScreenPadding,
                        end = Dimens.ScreenPadding,
                        top = Dimens.SpacingLarge,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingXLarge),
                ) {
                    items(items, key = { it.id }) { item ->
                        FeedItemRow(item = item, onClick = { onItemClick(item.id) })
                    }
                }
            }

            if (showPostedBanner) {
                ReportPostedBanner(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Dimens.SpacingXLarge),
                )
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
 * Tapping anywhere on the row opens the item.
 */
@Composable
fun FeedItemRow(
    item: LostItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Open details", role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemPhoto(
            photoUri = item.photoUri,
            modifier = Modifier.size(Dimens.FeedImageWidth, Dimens.FeedImageHeight),
        )
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
    EmptyState(message = message, modifier = modifier)
}

/**
 * Bottom area holding the "Report Item" button, aligned to the end like the prototype.
 */
@Composable
private fun ReportItemBar(
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(Dimens.ScreenPadding),
        contentAlignment = Alignment.CenterEnd,
    ) {
        GrayButton(text = "Report Item", onClick = onReportClick)
    }
}

// Interactive: use the preview's "Start Interactive Mode" button to tap the filter chips.
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FeedScreenPreview() {
    CampusLostAndFoundTheme {
        FeedScreen(
            items = SampleData.feedItems,
            showPostedBanner = false,
            onPostedBannerTimeout = {},
            onItemClick = {},
            onReportClick = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Empty feed")
@Composable
private fun FeedEmptyPreview() {
    CampusLostAndFoundTheme {
        FeedScreen(
            items = emptyList(),
            showPostedBanner = false,
            onPostedBannerTimeout = {},
            onItemClick = {},
            onReportClick = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Report posted")
@Composable
private fun FeedReportPostedPreview() {
    CampusLostAndFoundTheme {
        FeedContent(
            items = SampleData.feedItems,
            selectedFilter = null,
            showPostedBanner = true,
            onFilterSelect = {},
            onItemClick = {},
            onReportClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedItemRowPreview() {
    CampusLostAndFoundTheme {
        FeedItemRow(
            item = SampleData.feedItems.first(),
            onClick = {},
            modifier = Modifier.padding(Dimens.ScreenPadding),
        )
    }
}

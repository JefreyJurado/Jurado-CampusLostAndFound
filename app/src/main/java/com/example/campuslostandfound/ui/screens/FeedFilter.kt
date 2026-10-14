package com.example.campuslostandfound.ui.screens

import com.example.campuslostandfound.data.ItemStatus
import com.example.campuslostandfound.data.LostItem

/**
 * The chips above the Feed. Each option knows which items it keeps and what to say
 * when none are left, so the Feed itself has no filter-specific branches.
 */
enum class FeedFilter(val label: String, val emptyMessage: String) {
    ALL("All", "No items reported yet."),
    LOST("Lost", "No lost items reported yet."),
    FOUND("Found", "No found items reported yet."),
    SAVED("Saved", "No saved items yet.\nTap the heart on an item to save it here."),
    ;

    fun matches(item: LostItem, savedItemIds: Set<Int>): Boolean = when (this) {
        ALL -> true
        LOST -> item.status == ItemStatus.LOST
        FOUND -> item.status == ItemStatus.FOUND
        SAVED -> item.id in savedItemIds
    }
}

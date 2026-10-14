package com.example.campuslostandfound.data

import java.io.Serializable

enum class ItemStatus(val label: String) {
    LOST("Lost"),
    FOUND("Found"),
}

/**
 * One reported item. Serializable so lists of items can be kept with rememberSaveable.
 */
data class LostItem(
    val id: Int,
    val name: String,
    val category: String,
    val location: String,
    val status: ItemStatus,
    val postedAt: String,
    val description: String = "",
    val photoUri: String? = null,
) : Serializable

/**
 * One chat message. [itemId] says which item's conversation it belongs to.
 */
data class ChatMessage(
    val itemId: Int,
    val text: String,
    val isFromMe: Boolean,
) : Serializable

/**
 * What the user filled in on the Report Item form, handed up when they submit.
 */
data class ReportSubmission(
    val status: ItemStatus,
    val name: String,
    val category: String,
    val location: String,
    val photoUri: String?,
)

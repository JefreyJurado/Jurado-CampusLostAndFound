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
) : Serializable

data class ChatMessage(
    val text: String,
    val isFromMe: Boolean,
) : Serializable

/**
 * Hardcoded sample data used to render the screens. There is no backend yet,
 * so new reports and messages live only in UI state.
 */
object SampleData {
    val feedItems = listOf(
        LostItem(
            id = 1,
            name = "Black Headphones",
            category = "Electronics",
            location = "Library, 2nd Floor",
            status = ItemStatus.FOUND,
            postedAt = "Today, 10:42 AM",
            description = "Black over-ear headphones, small scratch on left cup.",
        ),
        LostItem(
            id = 2,
            name = "Blue Water Bottle",
            category = "Personal item",
            location = "Building A, Room 204",
            status = ItemStatus.LOST,
            postedAt = "Today, 9:15 AM",
            description = "Blue metal bottle with a university sticker.",
        ),
        LostItem(
            id = 3,
            name = "Student ID Card",
            category = "ID / Document",
            location = "Cafeteria",
            status = ItemStatus.LOST,
            postedAt = "Yesterday, 4:30 PM",
            description = "Lanyard is green. Name starts with \"M\".",
        ),
        LostItem(
            id = 4,
            name = "Denim Jacket",
            category = "Clothing",
            location = "Near the covered court",
            status = ItemStatus.LOST,
            postedAt = "Yesterday, 1:05 PM",
            description = "Light-wash denim jacket, size M.",
        ),
    )

    /** Starting messages for an item's conversation; items nobody has messaged about start empty. */
    fun conversationFor(itemId: Int): List<ChatMessage> = when (itemId) {
        1 -> listOf(
            ChatMessage("Hi, I found these near the study pods this morning.", isFromMe = false),
            ChatMessage("That's mine, thank you so much!", isFromMe = true),
        )
        else -> emptyList()
    }
}

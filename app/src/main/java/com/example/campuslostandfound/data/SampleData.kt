package com.example.campuslostandfound.data

enum class ItemStatus(val label: String) {
    LOST("Lost"),
    FOUND("Found"),
}

data class LostItem(
    val name: String,
    val location: String,
    val status: ItemStatus,
    val postedAt: String = "",
    val description: String = "",
)

data class ChatMessage(
    val text: String,
    val isFromMe: Boolean,
)

/**
 * Hardcoded sample data used to render the screens until real data is wired up.
 */
object SampleData {
    val feedItems = listOf(
        LostItem("Black Headphone", "Library, 2nd Floor", ItemStatus.FOUND),
        LostItem("Blue Water Bottle", "Building A, Room 204", ItemStatus.LOST),
        LostItem("Student ID Card", "Cafeteria", ItemStatus.LOST),
        LostItem("Denim Jacket", "Near the covered court", ItemStatus.LOST),
    )

    val detailItem = LostItem(
        name = "Black Headphones",
        location = "Library, 2nd floor",
        status = ItemStatus.FOUND,
        postedAt = "Today, 10:42 AM",
        description = "Black over-ear headphones, small scratch on left cup.",
    )

    val conversation = listOf(
        ChatMessage("Hi, I found these near the study pods this morning.", isFromMe = false),
        ChatMessage("That's mine, thank you so much!", isFromMe = true),
    )
}

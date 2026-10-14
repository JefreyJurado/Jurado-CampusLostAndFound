package com.example.campuslostandfound.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import com.example.campuslostandfound.data.ChatMessage
import com.example.campuslostandfound.data.LostItem
import com.example.campuslostandfound.data.ReportSubmission
import com.example.campuslostandfound.data.SampleData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * App-level UI state that more than one screen needs, kept in one place (single source of truth).
 *
 * Screens never change these values directly. They call the functions below, and Compose
 * re-draws whatever reads the changed state (the Observer pattern built into Compose state).
 * The class has no Android or navigation code, so its logic can be unit-tested on its own.
 *
 * @param now supplies the current time; tests pass a fixed date.
 */
@Stable
class CampusAppState(
    items: List<LostItem>,
    messages: List<ChatMessage>,
    savedItemIds: Set<Int> = emptySet(),
    reportJustPosted: Boolean = false,
    private val now: () -> Date = { Date() },
) {
    private val itemList = items.toMutableStateList()
    private val messageList = messages.toMutableStateList()

    /** Every item, newest report first. */
    val items: List<LostItem> get() = itemList

    /** Ids of the items the user saved with the heart on Item Detail. */
    var savedItemIds by mutableStateOf(savedItemIds)
        private set

    /** True right after a report is submitted, until the Feed's banner hides itself. */
    var reportJustPosted by mutableStateOf(reportJustPosted)
        private set

    fun findItem(itemId: Int): LostItem? = itemList.firstOrNull { it.id == itemId }

    fun isSaved(itemId: Int): Boolean = itemId in savedItemIds

    fun messagesFor(itemId: Int): List<ChatMessage> = messageList.filter { it.itemId == itemId }

    /** Adds the report to the top of the feed with the next free id, and raises the banner. */
    fun addReport(report: ReportSubmission): LostItem {
        val item = LostItem(
            id = (itemList.maxOfOrNull { it.id } ?: 0) + 1,
            name = report.name,
            category = report.category,
            location = report.location,
            status = report.status,
            postedAt = formatPostedAt(now()),
            photoUri = report.photoUri,
        )
        itemList.add(0, item)
        reportJustPosted = true
        return item
    }

    fun toggleSaved(itemId: Int) {
        savedItemIds = if (itemId in savedItemIds) savedItemIds - itemId else savedItemIds + itemId
    }

    /** Adds a message from the user. Blank text is ignored. */
    fun sendMessage(itemId: Int, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        messageList.add(ChatMessage(itemId = itemId, text = trimmed, isFromMe = true))
    }

    fun dismissPostedBanner() {
        reportJustPosted = false
    }

    companion object {
        /** Saves the state into a Bundle-friendly list so it survives rotation. */
        @Suppress("UNCHECKED_CAST")
        val Saver: Saver<CampusAppState, Any> = listSaver(
            save = {
                listOf(
                    ArrayList(it.itemList),
                    ArrayList(it.messageList),
                    ArrayList(it.savedItemIds),
                    it.reportJustPosted,
                )
            },
            restore = {
                CampusAppState(
                    items = it[0] as List<LostItem>,
                    messages = it[1] as List<ChatMessage>,
                    savedItemIds = (it[2] as List<Int>).toSet(),
                    reportJustPosted = it[3] as Boolean,
                )
            },
        )

        /** "Today, 3:05 PM" — reports made in the app are always made today. */
        fun formatPostedAt(date: Date): String =
            "Today, " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    }
}

/**
 * Creates the app state once and keeps it through recomposition and rotation.
 */
@Composable
fun rememberCampusAppState(): CampusAppState = rememberSaveable(saver = CampusAppState.Saver) {
    CampusAppState(items = SampleData.feedItems, messages = SampleData.messages)
}

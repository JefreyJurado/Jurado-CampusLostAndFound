package com.example.campuslostandfound.ui.state

import com.example.campuslostandfound.data.ItemStatus
import com.example.campuslostandfound.data.ReportSubmission
import com.example.campuslostandfound.data.SampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

/**
 * Plain JVM tests for the app-level state logic. No device or emulator needed.
 */
class CampusAppStateTest {

    private val fixedNow = Date(0)

    private fun newState() = CampusAppState(
        items = SampleData.feedItems,
        messages = SampleData.messages,
        now = { fixedNow },
    )

    private val umbrella = ReportSubmission(
        status = ItemStatus.LOST,
        name = "Umbrella",
        category = "Personal item",
        location = "Gym",
        photoUri = null,
    )

    @Test
    fun addReport_putsItemFirst_withNextIdAndBanner() {
        val state = newState()

        val added = state.addReport(umbrella)

        assertEquals(SampleData.feedItems.maxOf { it.id } + 1, added.id)
        assertEquals(added, state.items.first())
        assertEquals(SampleData.feedItems.size + 1, state.items.size)
        assertEquals(CampusAppState.formatPostedAt(fixedNow), added.postedAt)
        assertTrue(state.reportJustPosted)
    }

    @Test
    fun addReport_twice_givesDistinctIds() {
        val state = newState()

        val first = state.addReport(umbrella)
        val second = state.addReport(umbrella.copy(name = "Keys"))

        assertEquals(first.id + 1, second.id)
    }

    @Test
    fun addReport_onEmptyFeed_startsAtIdOne() {
        val state = CampusAppState(items = emptyList(), messages = emptyList())

        assertEquals(1, state.addReport(umbrella).id)
    }

    @Test
    fun dismissPostedBanner_clearsFlag() {
        val state = newState()
        state.addReport(umbrella)

        state.dismissPostedBanner()

        assertFalse(state.reportJustPosted)
    }

    @Test
    fun toggleSaved_savesThenUnsaves() {
        val state = newState()

        state.toggleSaved(2)
        assertTrue(state.isSaved(2))
        assertFalse(state.isSaved(1))

        state.toggleSaved(2)
        assertFalse(state.isSaved(2))
    }

    @Test
    fun sendMessage_addsTrimmedMessage_toThatItemOnly() {
        val state = newState()

        state.sendMessage(itemId = 2, text = "  Is it still there?  ")

        val messages = state.messagesFor(2)
        assertEquals(1, messages.size)
        assertEquals("Is it still there?", messages.single().text)
        assertTrue(messages.single().isFromMe)
        assertEquals(SampleData.messages.size, state.messagesFor(1).size)
    }

    @Test
    fun sendMessage_ignoresBlankText() {
        val state = newState()

        state.sendMessage(itemId = 2, text = "   ")

        assertTrue(state.messagesFor(2).isEmpty())
    }

    @Test
    fun findItem_returnsNullForUnknownId() {
        assertNull(newState().findItem(999))
    }
}

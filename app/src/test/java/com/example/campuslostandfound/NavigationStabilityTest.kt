package com.example.campuslostandfound

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Automated version of the Week 9 stability protocol, run against the whole app (MainActivity)
 * on the JVM with Robolectric, on a phone-sized screen. Covers:
 *  - rapid interaction: repeated taps open one screen, pop one screen, post one report
 *  - back stack: deep navigation unwinds in order; Report Item asks before discarding
 *  - re-entry: filter, saved items, and messages are still there after leaving and returning
 *  - rotation: the form and back stack survive the activity being recreated
 *  - full flow: the main journey run twice gives the same result
 *
 * Rapid taps are simulated by pausing the animation clock, so every tap lands while the first
 * transition is still running, which is when duplicate navigation would happen.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w411dp-h891dp-xxhdpi")
class NavigationStabilityTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun back() {
        rule.waitForIdle()
        rule.runOnUiThread { rule.activity.onBackPressedDispatcher.onBackPressed() }
        rule.waitForIdle()
    }

    private fun onFeed() = rule.onNodeWithText("Report Item").assertIsDisplayed()
    private fun count(text: String) = rule.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().size

    @Test
    fun rapidTapOnFeedItem_opensOneDetail() {
        rule.mainClock.autoAdvance = false
        repeat(5) { rule.onAllNodesWithText("Blue Water Bottle").onFirst().performClick() }
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        rule.onNodeWithText("Item Detail").assertIsDisplayed()
        back()
        onFeed()
        assertEquals(0, count("Item Detail"))
    }

    @Test
    fun rapidTapOnBackArrow_popsOnlyOnce() {
        rule.onAllNodesWithText("Black Headphones").onFirst().performClick()
        rule.onNodeWithText("Message finder").performClick()
        rule.waitForIdle()
        rule.mainClock.autoAdvance = false
        repeat(3) { rule.onNodeWithContentDescription("Back").performClick() }
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        rule.onNodeWithText("Item Detail").assertIsDisplayed()
    }

    @Test
    fun deepNavigation_unwindsInOrder() {
        rule.onAllNodesWithText("Black Headphones").onFirst().performClick()
        rule.onNodeWithText("Message finder").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Conversation").assertIsDisplayed()
        rule.onNodeWithText("That's mine, thank you so much!").assertIsDisplayed()
        back()
        rule.onNodeWithText("Item Detail").assertIsDisplayed()
        back()
        onFeed()
    }

    @Test
    fun otherItemsConversation_startsEmpty() {
        rule.onAllNodesWithText("Denim Jacket").onFirst().performClick()
        rule.onNodeWithText("Message owner").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("No messages yet.\nSay hi to start the conversation.").assertIsDisplayed()
    }

    @Test
    fun reportBackWithChanges_asksFirst_andKeepEditingKeepsText() {
        rule.onNodeWithText("Report Item").performClick()
        rule.onNodeWithText("Name").performTextInput("Umbrella")
        back() // system back
        rule.onNodeWithText("Discard this report?").assertIsDisplayed()
        rule.onNodeWithText("Keep Editing").performClick()
        rule.onNodeWithText("Umbrella").assertIsDisplayed()
        rule.onNodeWithContentDescription("Back").performClick() // arrow behaves the same
        rule.onNodeWithText("Discard").performClick()
        rule.waitForIdle()
        onFeed()
        assertEquals(0, count("Umbrella"))
    }

    @Test
    fun reportBackWithoutChanges_leavesImmediately() {
        rule.onNodeWithText("Report Item").performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Back").performClick()
        rule.waitForIdle()
        onFeed()
    }

    @Test
    fun submitIncomplete_explainsMissingFieldsAndStays() {
        rule.onNodeWithText("Report Item").performClick()
        rule.onNodeWithText("Name").performTextInput("Umbrella")
        rule.onNodeWithText("Submit Report").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Submit Report").assertIsDisplayed()
        // Errors are spelled out, and only for the fields still empty.
        assertEquals(0, count("Enter the item's name"))
        rule.onNodeWithText("Enter a category").assertIsDisplayed()
        rule.onNodeWithText("Enter where it was lost or found").assertIsDisplayed()
        // Filling a field clears its error straight away.
        rule.onNodeWithText("Category").performTextInput("Personal item")
        assertEquals(0, count("Enter a category"))
    }

    @Test
    fun keyboardSendKey_sendsMessage() {
        rule.onAllNodesWithText("Denim Jacket").onFirst().performClick()
        rule.onNodeWithText("Message owner").performClick()
        rule.onNodeWithText("Type a message").performTextInput("Found it near the gym")
        rule.onNodeWithText("Found it near the gym").performImeAction()
        rule.waitForIdle()
        // Sent: the box is empty again (placeholder back), and the text is now a message.
        rule.onNodeWithText("Type a message").assertIsDisplayed()
        assertEquals(1, count("Found it near the gym"))
        assertEquals(0, count("No messages yet.\nSay hi to start the conversation."))
    }

    @Test
    fun doubleSubmit_postsOneReport_andReturnsToFeed() {
        rule.onNodeWithText("Report Item").performClick()
        rule.onNodeWithText("Name").performTextInput("Umbrella")
        rule.onNodeWithText("Category").performTextInput("Personal item")
        rule.onNodeWithText("Location").performTextInput("Gym")
        rule.mainClock.autoAdvance = false
        repeat(3) { rule.onNodeWithText("Submit Report").performClick() }
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        onFeed()
        assertEquals(1, count("Umbrella"))
        rule.onNodeWithText("Report Posted").assertIsDisplayed()
        rule.mainClock.advanceTimeBy(2_500)
        assertEquals(0, count("Report Posted"))
        // New report opens like any other item and keeps the category.
        rule.onAllNodesWithText("Umbrella").onFirst().performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Item Detail").assertIsDisplayed()
        assertEquals(1, rule.onAllNodesWithText("Personal item", substring = true).fetchSemanticsNodes().size)
    }

    @Test
    fun feedFilter_survivesReturnFromDetail() {
        rule.onNode(hasText("Lost") and isSelectable()).performClick()
        assertEquals(0, count("Black Headphones"))
        rule.onAllNodesWithText("Denim Jacket").onFirst().performClick()
        rule.waitForIdle()
        back()
        rule.onNode(hasText("Lost") and isSelectable()).assertIsSelected()
        assertEquals(0, count("Black Headphones"))
    }

    @Test
    fun savedItem_survivesReEntry() {
        rule.onAllNodesWithText("Black Headphones").onFirst().performClick()
        rule.onNodeWithContentDescription("Save item").performClick()
        rule.onNodeWithText("Saved to your list").assertIsDisplayed()
        back()
        rule.onAllNodesWithText("Black Headphones").onFirst().performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Saved to your list").assertIsDisplayed()
        back()
        rule.onAllNodesWithText("Blue Water Bottle").onFirst().performClick()
        rule.waitForIdle()
        assertEquals(0, count("Saved to your list"))
    }

    @Test
    fun savedChip_showsOnlySavedItems() {
        rule.onNode(hasText("Saved") and isSelectable()).performClick()
        rule.onNodeWithText("No saved items yet.\nTap the heart on an item to save it here.").assertIsDisplayed()
        rule.onNode(hasText("All") and isSelectable()).performClick()

        rule.onAllNodesWithText("Denim Jacket").onFirst().performClick()
        rule.onNodeWithContentDescription("Save item").performClick()
        back()
        rule.onNode(hasText("Saved") and isSelectable()).performClick()

        assertEquals(1, count("Denim Jacket"))
        assertEquals(0, count("Black Headphones"))
        assertEquals(0, count("Blue Water Bottle"))
    }

    @Test
    fun sentMessage_survivesReEntry() {
        rule.onAllNodesWithText("Black Headphones").onFirst().performClick()
        rule.onNodeWithText("Message finder").performClick()
        rule.onNodeWithText("Type a message").performTextInput("See you at 3")
        rule.mainClock.autoAdvance = false
        repeat(3) { rule.onNodeWithText("Send").performClick() }
        rule.mainClock.autoAdvance = true
        rule.waitForIdle()
        assertEquals(1, count("See you at 3"))
        back()
        rule.onNodeWithText("Message finder").performClick()
        rule.waitForIdle()
        assertEquals(1, count("See you at 3"))
    }

    @Test
    fun rotation_keepsStackAndState() {
        rule.onNodeWithText("Report Item").performClick()
        rule.onNodeWithText("Name").performTextInput("Umbrella")
        rule.activityRule.scenario.recreate()
        rule.waitForIdle()
        rule.onNodeWithText("Umbrella").assertIsDisplayed()
        back()
        rule.onNodeWithText("Discard this report?").assertIsDisplayed()
    }

    @Test
    fun fullFlow_twice_isConsistent() {
        repeat(2) {
            rule.onAllNodesWithText("Black Headphones").onFirst().performClick()
            rule.onNodeWithText("Message finder").performClick()
            rule.waitForIdle()
            rule.onNodeWithText("Conversation").assertIsDisplayed()
            back(); rule.onNodeWithText("Item Detail").assertIsDisplayed()
            back(); onFeed()
            rule.onNodeWithText("Report Item").performClick()
            rule.waitForIdle()
            rule.onNodeWithText("Submit Report").assertIsDisplayed()
            back(); onFeed()
        }
    }
}

package com.example.campuslostandfound.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campuslostandfound.data.ChatMessage
import com.example.campuslostandfound.data.LostItem
import com.example.campuslostandfound.data.SampleData
import com.example.campuslostandfound.ui.screens.ConversationScreen
import com.example.campuslostandfound.ui.screens.FeedScreen
import com.example.campuslostandfound.ui.screens.ItemDetailScreen
import com.example.campuslostandfound.ui.screens.ReportItemScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The app's single navigation graph. All navigate / popBackStack calls live here,
 * and every one of them runs inside a user event callback, never during composition.
 *
 * Flow:  Feed ─┬─> Report Item            (back / submit → Feed)
 *              └─> Item Detail ─> Conversation
 *
 * App-level state is kept here because more than one screen needs it, and it must outlive
 * any single screen. rememberSaveable keeps it through rotation.
 *  - items          → the feed; new reports are added to the top
 *  - savedItemIds   → items the user saved with the heart on Item Detail
 *  - messages       → every conversation's messages, tagged with their item id
 *  - reportJustPosted → shows the "Report Posted" banner on the Feed after a submit
 */
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val items = rememberSaveable(saver = snapshotListSaver()) { SampleData.feedItems.toMutableStateList() }
    val messages = rememberSaveable(saver = snapshotListSaver()) { SampleData.messages.toMutableStateList() }
    var savedItemIds by rememberSaveable { mutableStateOf(emptyList<Int>()) }
    var reportJustPosted by rememberSaveable { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Routes.FEED,
        modifier = modifier,
    ) {
        composable(Routes.FEED) { entry ->
            FeedScreen(
                items = items,
                showPostedBanner = reportJustPosted,
                onPostedBannerTimeout = { reportJustPosted = false },
                onItemClick = { itemId ->
                    entry.ifResumed { navController.navigate(Routes.itemDetail(itemId)) }
                },
                onReportClick = {
                    entry.ifResumed { navController.navigate(Routes.REPORT_ITEM) }
                },
            )
        }

        composable(Routes.REPORT_ITEM) { entry ->
            ReportItemScreen(
                onNavigateBack = {
                    entry.ifResumed { navController.popBackStack() }
                },
                onSubmit = { report ->
                    // Guarded too, so a double tap on Submit cannot post the same report twice.
                    entry.ifResumed {
                        items.add(
                            0,
                            LostItem(
                                id = (items.maxOfOrNull { it.id } ?: 0) + 1,
                                name = report.name,
                                category = report.category,
                                location = report.location,
                                status = report.status,
                                photoUri = report.photoUri,
                                postedAt = "Today, " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                            ),
                        )
                        reportJustPosted = true
                        navController.popBackStack()
                    }
                },
            )
        }

        composable(
            route = Routes.ITEM_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_ITEM_ID) { type = NavType.IntType }),
        ) { entry ->
            val itemId = entry.arguments?.getInt(Routes.ARG_ITEM_ID)
            // Ids only ever come from items in the list, so this lookup always succeeds.
            val item = items.firstOrNull { it.id == itemId } ?: return@composable

            ItemDetailScreen(
                item = item,
                isSaved = item.id in savedItemIds,
                onSaveClick = {
                    savedItemIds = if (item.id in savedItemIds) savedItemIds - item.id else savedItemIds + item.id
                },
                onBackClick = {
                    entry.ifResumed { navController.popBackStack() }
                },
                onMessageClick = {
                    entry.ifResumed { navController.navigate(Routes.conversation(item.id)) }
                },
            )
        }

        composable(
            route = Routes.CONVERSATION,
            arguments = listOf(navArgument(Routes.ARG_ITEM_ID) { type = NavType.IntType }),
        ) { entry ->
            val itemId = entry.arguments?.getInt(Routes.ARG_ITEM_ID) ?: return@composable

            ConversationScreen(
                messages = messages.filter { it.itemId == itemId },
                onSendMessage = { text ->
                    messages.add(ChatMessage(itemId = itemId, text = text, isFromMe = true))
                },
                onBackClick = {
                    entry.ifResumed { navController.popBackStack() }
                },
            )
        }
    }
}

/**
 * Runs [action] only while this destination is the one on screen and settled (RESUMED).
 * As soon as a navigation starts, the leaving screen drops below RESUMED, so extra taps that
 * arrive during the transition are ignored. This is what stops rapid tapping from stacking
 * duplicate screens or popping more than one screen per tap.
 */
private fun NavBackStackEntry.ifResumed(action: () -> Unit) {
    if (lifecycle.currentState == Lifecycle.State.RESUMED) action()
}

/** Saves a SnapshotStateList by copying its elements (which must be saveable, e.g. Serializable). */
private fun <T : Any> snapshotListSaver() = listSaver<SnapshotStateList<T>, T>(
    save = { it.toList() },
    restore = { it.toMutableStateList() },
)

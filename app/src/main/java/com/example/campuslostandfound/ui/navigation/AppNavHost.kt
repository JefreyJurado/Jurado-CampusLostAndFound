package com.example.campuslostandfound.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campuslostandfound.ui.screens.ConversationScreen
import com.example.campuslostandfound.ui.screens.FeedScreen
import com.example.campuslostandfound.ui.screens.ItemDetailScreen
import com.example.campuslostandfound.ui.screens.ReportItemScreen
import com.example.campuslostandfound.ui.state.CampusAppState
import com.example.campuslostandfound.ui.state.rememberCampusAppState

/**
 * The app's single navigation graph. All navigate / popBackStack calls live here,
 * and every one of them runs inside a user event callback, never during composition.
 *
 * Flow:  Feed ─┬─> Report Item            (back / submit → Feed)
 *              └─> Item Detail ─> Conversation
 *
 * Data shared between screens comes from [CampusAppState]; this file only connects
 * screen events to state changes and navigation.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    appState: CampusAppState = rememberCampusAppState(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.FEED,
        modifier = modifier,
    ) {
        composable(Routes.FEED) { entry ->
            FeedScreen(
                items = appState.items,
                savedItemIds = appState.savedItemIds,
                showPostedBanner = appState.reportJustPosted,
                onPostedBannerTimeout = appState::dismissPostedBanner,
                onItemClick = { itemId -> navController.navigateFrom(entry, Routes.itemDetail(itemId)) },
                onReportClick = { navController.navigateFrom(entry, Routes.REPORT_ITEM) },
            )
        }

        composable(Routes.REPORT_ITEM) { entry ->
            ReportItemScreen(
                onNavigateBack = { navController.popBackStackFrom(entry) },
                onSubmit = { report ->
                    // Guarded too, so a double tap on Submit cannot post the same report twice.
                    entry.ifResumed {
                        appState.addReport(report)
                        navController.popBackStack()
                    }
                },
            )
        }

        composable(Routes.ITEM_DETAIL, arguments = itemIdArgument) { entry ->
            // Ids only ever come from items in the list, so this lookup always succeeds.
            val item = appState.findItem(entry.itemId()) ?: return@composable

            ItemDetailScreen(
                item = item,
                isSaved = appState.isSaved(item.id),
                onSaveClick = { appState.toggleSaved(item.id) },
                onBackClick = { navController.popBackStackFrom(entry) },
                onMessageClick = { navController.navigateFrom(entry, Routes.conversation(item.id)) },
            )
        }

        composable(Routes.CONVERSATION, arguments = itemIdArgument) { entry ->
            val itemId = entry.itemId()

            ConversationScreen(
                messages = appState.messagesFor(itemId),
                onSendMessage = { text -> appState.sendMessage(itemId, text) },
                onBackClick = { navController.popBackStackFrom(entry) },
            )
        }
    }
}

/** The one argument used in this graph: the id of the item a screen shows. */
private val itemIdArgument = listOf(navArgument(Routes.ARG_ITEM_ID) { type = NavType.IntType })

private fun NavBackStackEntry.itemId(): Int =
    requireNotNull(arguments?.getInt(Routes.ARG_ITEM_ID)) { "Route is missing ${Routes.ARG_ITEM_ID}" }

/**
 * Runs [action] only while this destination is the one on screen and settled (RESUMED).
 * As soon as a navigation starts, the leaving screen drops below RESUMED, so extra taps that
 * arrive during the transition are ignored. This is what stops rapid tapping from stacking
 * duplicate screens or popping more than one screen per tap.
 */
private fun NavBackStackEntry.ifResumed(action: () -> Unit) {
    if (lifecycle.currentState == Lifecycle.State.RESUMED) action()
}

/** Navigates to [route], ignoring the tap if [from] is no longer the settled screen. */
private fun NavController.navigateFrom(from: NavBackStackEntry, route: String) =
    from.ifResumed { navigate(route) }

/** Goes back one screen, ignoring the tap if [from] is no longer the settled screen. */
private fun NavController.popBackStackFrom(from: NavBackStackEntry) =
    from.ifResumed { popBackStack() }

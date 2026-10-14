package com.example.campuslostandfound.ui.navigation

/**
 * Every navigation route in the app. Screens never build route strings themselves;
 * they call the helpers below so a route can only be spelled one way.
 */
object Routes {
    const val ARG_ITEM_ID = "itemId"

    private const val ITEM_DETAIL_BASE = "item_detail"
    private const val CONVERSATION_BASE = "conversation"

    const val FEED = "feed"
    const val REPORT_ITEM = "report_item"
    const val ITEM_DETAIL = "$ITEM_DETAIL_BASE/{$ARG_ITEM_ID}"
    const val CONVERSATION = "$CONVERSATION_BASE/{$ARG_ITEM_ID}"

    fun itemDetail(itemId: Int) = "$ITEM_DETAIL_BASE/$itemId"
    fun conversation(itemId: Int) = "$CONVERSATION_BASE/$itemId"
}

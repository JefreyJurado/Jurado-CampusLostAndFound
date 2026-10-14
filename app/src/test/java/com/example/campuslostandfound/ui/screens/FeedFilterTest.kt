package com.example.campuslostandfound.ui.screens

import com.example.campuslostandfound.data.SampleData
import org.junit.Assert.assertEquals
import org.junit.Test

class FeedFilterTest {

    private val items = SampleData.feedItems

    private fun namesFor(filter: FeedFilter, saved: Set<Int> = emptySet()) =
        items.filter { filter.matches(it, saved) }.map { it.name }

    @Test
    fun all_keepsEveryItem() {
        assertEquals(items.map { it.name }, namesFor(FeedFilter.ALL))
    }

    @Test
    fun lostAndFound_splitByStatus() {
        assertEquals(listOf("Black Headphones"), namesFor(FeedFilter.FOUND))
        assertEquals(listOf("Blue Water Bottle", "Student ID Card", "Denim Jacket"), namesFor(FeedFilter.LOST))
    }

    @Test
    fun saved_keepsOnlySavedIds() {
        assertEquals(listOf("Black Headphones", "Denim Jacket"), namesFor(FeedFilter.SAVED, saved = setOf(1, 4)))
        assertEquals(emptyList<String>(), namesFor(FeedFilter.SAVED))
    }
}

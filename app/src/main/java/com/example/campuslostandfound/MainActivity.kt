package com.example.campuslostandfound

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campuslostandfound.data.SampleData
import com.example.campuslostandfound.ui.screens.FeedScreen
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusLostAndFoundTheme {
                // Feed is the entry point. Navigation to the other screens comes in a later week.
                FeedScreen(items = SampleData.feedItems)
            }
        }
    }
}

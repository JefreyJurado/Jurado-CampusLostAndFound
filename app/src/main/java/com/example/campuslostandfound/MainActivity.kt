package com.example.campuslostandfound

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campuslostandfound.ui.navigation.AppNavHost
import com.example.campuslostandfound.ui.theme.CampusLostAndFoundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusLostAndFoundTheme {
                AppNavHost()
            }
        }
    }
}

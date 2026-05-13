package com.healthtracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.healthtracker.app.ui.HealthTrackerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val graph = (application as HealthTrackerApplication).graph
        setContent {
            HealthTrackerApp(graph)
        }
    }
}

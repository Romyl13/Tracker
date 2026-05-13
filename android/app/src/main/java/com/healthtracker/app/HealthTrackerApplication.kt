package com.healthtracker.app

import android.app.Application
import com.healthtracker.app.di.AppGraph

class HealthTrackerApplication : Application() {
    lateinit var graph: AppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        graph = AppGraph(this)
    }
}

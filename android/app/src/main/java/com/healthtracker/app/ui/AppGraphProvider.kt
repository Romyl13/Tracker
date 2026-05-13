package com.healthtracker.app.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.healthtracker.app.di.AppGraph

val LocalAppGraph = staticCompositionLocalOf<AppGraph> {
    error("LocalAppGraph not provided")
}

package com.healthtracker.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val route: String,
    val icon: ImageVector,
    val titleRes: Int
) {
    Home("home", Icons.Outlined.Home, com.healthtracker.app.R.string.nav_home),
    Community("community", Icons.Outlined.Groups, com.healthtracker.app.R.string.nav_community),
    Analysis("analysis", Icons.Outlined.BarChart, com.healthtracker.app.R.string.nav_analysis),
    Profile("profile", Icons.Outlined.Person, com.healthtracker.app.R.string.nav_profile)
}

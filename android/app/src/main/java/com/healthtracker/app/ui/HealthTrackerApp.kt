package com.healthtracker.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.healthtracker.app.ui.analysis.AnalysisScreen
import com.healthtracker.app.ui.analysis.AnalysisViewModel
import com.healthtracker.app.ui.community.CommunityScreen
import com.healthtracker.app.ui.community.CommunityViewModel
import com.healthtracker.app.ui.home.HomeScreen
import com.healthtracker.app.ui.home.HomeViewModel
import com.healthtracker.app.ui.navigation.AppDestination
import com.healthtracker.app.ui.profile.ProfileScreen
import com.healthtracker.app.ui.profile.ProfileViewModel
import com.healthtracker.app.ui.theme.HealthTrackerTheme

@Composable
fun HealthTrackerApp(graph: com.healthtracker.app.di.AppGraph) {
    HealthTrackerTheme {
        CompositionLocalProvider(LocalAppGraph provides graph) {
            val navController = rememberNavController()
            val backStack by navController.currentBackStackEntryAsState()
            val currentRoute = backStack?.destination?.route

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        AppDestination.entries.forEach { dest ->
                            NavigationBarItem(
                                selected = currentRoute == dest.route,
                                onClick = {
                                    if (currentRoute != dest.route) {
                                        navController.navigate(dest.route) {
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { Icon(dest.icon, contentDescription = null) },
                                label = { Text(stringResource(dest.titleRes)) }
                            )
                        }
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = AppDestination.Home.route,
                    modifier = Modifier.padding(padding)
                ) {
                    addGraph(graph)
                }
            }
        }
    }
}

private fun NavGraphBuilder.addGraph(graph: com.healthtracker.app.di.AppGraph) {
    composable(AppDestination.Home.route) {
        val vm: HomeViewModel = viewModel(
            factory = HomeViewModel.factory(
                graph.habitRepository,
                graph.profileRepository,
                graph.communityRepository
            )
        )
        LaunchedEffect(Unit) { vm.refresh() }
        HomeScreen(vm)
    }
    composable(AppDestination.Community.route) {
        val vm: CommunityViewModel = viewModel(factory = CommunityViewModel.factory(graph.communityRepository))
        LaunchedEffect(Unit) { vm.refresh() }
        CommunityScreen(vm)
    }
    composable(AppDestination.Analysis.route) {
        val vm: AnalysisViewModel = viewModel(factory = AnalysisViewModel.factory(graph.analyticsRepository))
        LaunchedEffect(Unit) { vm.refresh() }
        val state by vm.state.collectAsStateWithLifecycle()
        AnalysisScreen(summary = state, onRefresh = { vm.refresh() })
    }
    composable(AppDestination.Profile.route) {
        val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(graph.profileRepository))
        LaunchedEffect(Unit) { vm.refresh() }
        ProfileScreen(vm)
    }
}

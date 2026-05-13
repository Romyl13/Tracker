package com.healthtracker.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthtracker.app.data.repo.CommunityRepository
import com.healthtracker.app.data.repo.HabitRepository
import com.healthtracker.app.data.repo.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val habits: HabitRepository,
    private val profile: ProfileRepository,
    private val community: CommunityRepository
) : ViewModel() {

    val logs = habits.observeLogs().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val profileState = profile.observeProfile().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun refresh() {
        viewModelScope.launch {
            profile.seedIfEmpty()
            community.ensureSeeded()
        }
    }

    fun checkIn(note: String?) {
        viewModelScope.launch { habits.checkIn(note) }
    }

    fun relapse(bucket: String, stress: Int, reason: String) {
        viewModelScope.launch { habits.relapse(bucket, stress, reason) }
    }

    companion object {
        fun factory(
            habits: HabitRepository,
            profile: ProfileRepository,
            community: CommunityRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HomeViewModel(habits, profile, community) as T
            }
    }
}

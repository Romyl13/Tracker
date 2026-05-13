package com.healthtracker.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthtracker.app.data.repo.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profile: ProfileRepository
) : ViewModel() {

    val profileState = profile.observeProfile().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun refresh() {
        viewModelScope.launch { profile.seedIfEmpty() }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { profile.setNotifications(enabled) }
    }

    fun signOut() {
        viewModelScope.launch { profile.clearLocalIdentity() }
    }

    companion object {
        fun factory(profile: ProfileRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProfileViewModel(profile) as T
            }
    }
}

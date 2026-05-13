package com.healthtracker.app.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthtracker.app.data.repo.CommunityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val repo: CommunityRepository
) : ViewModel() {

    val posts = repo.observePosts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun refresh() {
        viewModelScope.launch { repo.refresh() }
    }

    fun upvote(id: String) {
        viewModelScope.launch { repo.upvote(id) }
    }

    companion object {
        fun factory(repo: CommunityRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CommunityViewModel(repo) as T
            }
    }
}

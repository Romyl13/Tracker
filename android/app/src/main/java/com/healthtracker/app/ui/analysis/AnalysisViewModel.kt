package com.healthtracker.app.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.healthtracker.app.data.local.model.LocalAnalyticsSummary
import com.healthtracker.app.data.repo.AnalyticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnalysisViewModel(
    private val analytics: AnalyticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow<LocalAnalyticsSummary?>(null)
    val state: StateFlow<LocalAnalyticsSummary?> = _state

    fun refresh() {
        viewModelScope.launch {
            _state.value = analytics.loadSummary()
        }
    }

    companion object {
        fun factory(repo: AnalyticsRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AnalysisViewModel(repo) as T
            }
    }
}

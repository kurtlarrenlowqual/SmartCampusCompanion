package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.repository.AnnouncementRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementViewModel(private val repo: AnnouncementRepository) : ViewModel() {

    val announcements: StateFlow<List<com.example.smartcampuscompanion.data.local.AnnouncementEntity>> =
        repo.observeAnnouncements()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> =
        repo.observeUnreadCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun seedIfNeeded() {
        viewModelScope.launch { repo.ensureSeedData() }
    }

    fun markRead(id: Int) {
        viewModelScope.launch { repo.markRead(id) }
    }

    fun markAllRead() {
        viewModelScope.launch { repo.markAllRead() }
    }
}

class AnnouncementViewModelFactory(private val repo: AnnouncementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnnouncementViewModel(repo) as T
    }
}
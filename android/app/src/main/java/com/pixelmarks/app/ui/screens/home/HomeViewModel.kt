package com.pixelmarks.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelmarks.app.data.model.*
import com.pixelmarks.app.data.repository.BookmarkRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: BookmarkRepository
) : ViewModel() {

    private val _currentView = MutableStateFlow(SmartView.ALL)
    val currentView = _currentView.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag = _selectedTag.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode = _viewMode.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val collections = repository.collections.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val baseBookmarks = _currentView.flatMapLatest { view ->
        when (view) {
            SmartView.ALL -> repository.allBookmarks
            SmartView.FAVORITES -> repository.favorites
            SmartView.REMINDERS -> repository.reminders
            SmartView.UNSORTED -> repository.unsorted
            SmartView.ARCHIVE -> repository.archive
            SmartView.COLLECTION -> repository.allBookmarks
        }
    }

    val bookmarks = combine(baseBookmarks, _searchQuery, _selectedTag) { list, query, tag ->
        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.domain.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)
            val matchesTag = tag == null || item.tags.contains(tag)
            matchesQuery && matchesTag
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setView(view: SmartView) {
        _currentView.value = view
        clearSelection()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTag(tag: String?) {
        _selectedTag.value = tag
    }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
    }

    fun quickAddBookmark(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.saveBookmark(url = url, fetchMetadata = true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.toggleFavorite(bookmark)
        }
    }

    fun toggleArchive(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.toggleArchive(bookmark)
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.deleteBookmarks(listOf(bookmark.id))
        }
    }

    fun refreshMetadata(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.refreshMetadata(bookmark.id)
        }
    }

    // Selection mode
    fun toggleSelection(id: Long) {
        _selectedIds.value = if (_selectedIds.value.contains(id)) {
            _selectedIds.value - id
        } else {
            _selectedIds.value + id
        }
    }

    fun selectAll() {
        _selectedIds.value = bookmarks.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun bulkFavorite() {
        val ids = _selectedIds.value.toList()
        viewModelScope.launch {
            for (id in ids) {
                val b = bookmarks.value.find { it.id == id }
                if (b != null) repository.toggleFavorite(b)
            }
            clearSelection()
        }
    }

    fun bulkArchive() {
        val ids = _selectedIds.value.toList()
        viewModelScope.launch {
            for (id in ids) {
                val b = bookmarks.value.find { it.id == id }
                if (b != null) repository.toggleArchive(b)
            }
            clearSelection()
        }
    }

    fun bulkRefresh() {
        val ids = _selectedIds.value.toList()
        viewModelScope.launch {
            for (id in ids) {
                repository.refreshMetadata(id)
            }
            clearSelection()
        }
    }

    fun bulkDelete() {
        val ids = _selectedIds.value.toList()
        viewModelScope.launch {
            repository.deleteBookmarks(ids)
            clearSelection()
        }
    }
}

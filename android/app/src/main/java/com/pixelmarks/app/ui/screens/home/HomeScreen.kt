package com.pixelmarks.app.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelmarks.app.data.model.Bookmark
import com.pixelmarks.app.data.model.SmartView
import com.pixelmarks.app.data.model.ViewMode
import com.pixelmarks.app.ui.components.MorphingLoadingIndicator
import com.pixelmarks.app.ui.components.SelectionToolbar
import com.pixelmarks.app.ui.screens.home.components.BookmarkGridCard
import com.pixelmarks.app.ui.screens.home.components.BookmarkListRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCollections: () -> Unit,
    onOpenAddBookmark: () -> Unit,
    onOpenEditBookmark: (Bookmark) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentView by viewModel.currentView.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val collections by viewModel.collections.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isSelectionMode = selectedIds.isNotEmpty()
    var quickAddText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // M3 Expressive SearchBar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = { },
                    active = false,
                    onActiveChange = { },
                    placeholder = { Text("Search your bookmarks…") },
                    leadingIcon = {
                        if (isLoading) {
                            MorphingLoadingIndicator(size = 20.dp)
                        } else {
                            Icon(Icons.Rounded.Search, contentDescription = "Search")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(percent = 50)
                ) { }

                Spacer(modifier = Modifier.height(12.dp))

                // Smart View Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentView == SmartView.ALL,
                        onClick = { viewModel.setView(SmartView.ALL) },
                        label = { Text("All") },
                        leadingIcon = { Icon(Icons.Rounded.Bookmarks, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    FilterChip(
                        selected = currentView == SmartView.FAVORITES,
                        onClick = { viewModel.setView(SmartView.FAVORITES) },
                        label = { Text("Favorites") },
                        leadingIcon = { Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    FilterChip(
                        selected = currentView == SmartView.REMINDERS,
                        onClick = { viewModel.setView(SmartView.REMINDERS) },
                        label = { Text("Reminders") },
                        leadingIcon = { Icon(Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    FilterChip(
                        selected = currentView == SmartView.UNSORTED,
                        onClick = { viewModel.setView(SmartView.UNSORTED) },
                        label = { Text("Unsorted") },
                        leadingIcon = { Icon(Icons.Rounded.Inbox, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    FilterChip(
                        selected = currentView == SmartView.ARCHIVE,
                        onClick = { viewModel.setView(SmartView.ARCHIVE) },
                        label = { Text("Archive") },
                        leadingIcon = { Icon(Icons.Rounded.Archive, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = onOpenAddBookmark,
                    icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                    text = { Text("New bookmark", fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(20.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        bottomBar = {
            SelectionToolbar(
                visible = isSelectionMode,
                selectedCount = selectedIds.size,
                onClose = { viewModel.clearSelection() },
                onSelectAll = { viewModel.selectAll() },
                onFavorite = { viewModel.bulkFavorite() },
                onArchive = { viewModel.bulkArchive() },
                onRefresh = { viewModel.bulkRefresh() },
                onDelete = { viewModel.bulkDelete() },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Expressive Hero / Quick-Add Card
            if (currentView == SmartView.ALL && searchQuery.isBlank()) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Save any link",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Auto-detects title, description, and website thumbnail",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Add Input field
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerLowest,
                                    RoundedCornerShape(percent = 50)
                                )
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Link,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = quickAddText,
                                onValueChange = { quickAddText = it },
                                placeholder = { Text("Paste link to save instantly") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    if (quickAddText.isNotBlank()) {
                                        viewModel.quickAddBookmark(quickAddText)
                                        quickAddText = ""
                                    }
                                },
                                shape = RoundedCornerShape(percent = 50),
                                enabled = quickAddText.isNotBlank() && !isLoading
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }

            // Header row with counts and Grid/List toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${bookmarks.size} bookmarks",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(onClick = { viewModel.toggleViewMode() }) {
                    Icon(
                        imageVector = if (viewMode == ViewMode.GRID) Icons.Rounded.ViewAgenda else Icons.Rounded.GridView,
                        contentDescription = "Toggle layout"
                    )
                }
            }

            // Bookmarks content (Grid or List)
            if (bookmarks.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkAdd,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No bookmarks yet",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Save links from Chrome, YouTube, or paste above",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (viewMode == ViewMode.GRID) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bookmarks, key = { it.id }) { bookmark ->
                        val collection = collections.find { it.id == bookmark.collectionId }
                        BookmarkGridCard(
                            bookmark = bookmark,
                            collection = collection,
                            isSelected = selectedIds.contains(bookmark.id),
                            isSelectionMode = isSelectionMode,
                            onSelect = { viewModel.toggleSelection(bookmark.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(bookmark) },
                            onEdit = { onOpenEditBookmark(bookmark) },
                            onDelete = { viewModel.deleteBookmark(bookmark) },
                            onRefreshMetadata = { viewModel.refreshMetadata(bookmark) }
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(bookmarks, key = { it.id }) { bookmark ->
                        val collection = collections.find { it.id == bookmark.collectionId }
                        BookmarkListRow(
                            bookmark = bookmark,
                            collection = collection,
                            isSelected = selectedIds.contains(bookmark.id),
                            isSelectionMode = isSelectionMode,
                            onSelect = { viewModel.toggleSelection(bookmark.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(bookmark) },
                            onEdit = { onOpenEditBookmark(bookmark) },
                            onDelete = { viewModel.deleteBookmark(bookmark) },
                            onRefreshMetadata = { viewModel.refreshMetadata(bookmark) }
                        )
                    }
                }
            }
        }
    }
}

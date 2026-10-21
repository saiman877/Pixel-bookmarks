package com.pixelmarks.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pixelmarks.app.data.model.Bookmark
import com.pixelmarks.app.ui.navigation.Screen
import com.pixelmarks.app.ui.screens.collections.CollectionsScreen
import com.pixelmarks.app.ui.screens.editor.BookmarkEditorSheet
import com.pixelmarks.app.ui.screens.home.HomeScreen
import com.pixelmarks.app.ui.screens.home.HomeViewModel
import com.pixelmarks.app.ui.theme.PixelmarksTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as PixelmarksApplication
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(app.repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle incoming shared link from another app (e.g. YouTube, Chrome, Twitter, Reddit)
        val sharedUrl = extractSharedUrl(intent)

        setContent {
            PixelmarksTheme {
                val navController = rememberNavController()
                val collections by homeViewModel.collections.collectAsState()

                var showAddSheet by remember { mutableStateOf(sharedUrl != null) }
                var editingBookmark by remember { mutableStateOf<Bookmark?>(null) }
                var initialSheetUrl by remember { mutableStateOf(sharedUrl) }
                var currentRoute by remember { mutableStateOf(Screen.Home.route) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == Screen.Home.route,
                                onClick = {
                                    currentRoute = Screen.Home.route
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                },
                                icon = { Icon(Icons.Rounded.Bookmarks, contentDescription = "Bookmarks") },
                                label = { Text("Bookmarks") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == Screen.Collections.route,
                                onClick = {
                                    currentRoute = Screen.Collections.route
                                    navController.navigate(Screen.Collections.route)
                                },
                                icon = { Icon(Icons.Rounded.CollectionsBookmark, contentDescription = "Collections") },
                                label = { Text("Collections") }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToCollections = {
                                    currentRoute = Screen.Collections.route
                                    navController.navigate(Screen.Collections.route)
                                },
                                onOpenAddBookmark = {
                                    initialSheetUrl = null
                                    editingBookmark = null
                                    showAddSheet = true
                                },
                                onOpenEditBookmark = { bookmark ->
                                    editingBookmark = bookmark
                                    showAddSheet = true
                                }
                            )
                        }

                        composable(Screen.Collections.route) {
                            CollectionsScreen(
                                collections = collections,
                                onSelectCollection = { },
                                onAddNewCollection = { }
                            )
                        }
                    }

                    // Add / Edit ModalBottomSheet
                    if (showAddSheet) {
                        BookmarkEditorSheet(
                            bookmark = editingBookmark,
                            initialUrl = initialSheetUrl,
                            collections = collections,
                            onDismiss = { showAddSheet = false },
                            onSave = { url, title, description, colId, isFav ->
                                homeViewModel.quickAddBookmark(url)
                            },
                            onDelete = editingBookmark?.let { b ->
                                {
                                    homeViewModel.deleteBookmark(b)
                                    showAddSheet = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun extractSharedUrl(intent: Intent?): String? {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
            val urlRegex = Regex("""https?://[^\s<>"']+""", RegexOption.IGNORE_CASE)
            return urlRegex.find(text)?.value ?: text.trim()
        }
        return null
    }
}

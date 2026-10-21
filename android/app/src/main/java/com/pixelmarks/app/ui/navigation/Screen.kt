package com.pixelmarks.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Collections : Screen("collections")
    data object Settings : Screen("settings")
}

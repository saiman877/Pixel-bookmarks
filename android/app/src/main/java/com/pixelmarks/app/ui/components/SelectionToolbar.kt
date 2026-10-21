package com.pixelmarks.app.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectionToolbar(
    visible: Boolean,
    selectedCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onFavorite: () -> Unit,
    onArchive: () -> Unit,
    onRefresh: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it * 2 }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it * 2 }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(percent = 50),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = "Close")
                }

                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(onClick = onSelectAll) {
                    Icon(imageVector = Icons.Rounded.SelectAll, contentDescription = "Select all")
                }

                IconButton(onClick = onFavorite) {
                    Icon(imageVector = Icons.Rounded.Star, contentDescription = "Favorite")
                }

                IconButton(onClick = onArchive) {
                    Icon(imageVector = Icons.Rounded.Archive, contentDescription = "Archive")
                }

                IconButton(onClick = onRefresh) {
                    Icon(imageVector = Icons.Rounded.Refresh, contentDescription = "Refresh metadata & thumbnails")
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

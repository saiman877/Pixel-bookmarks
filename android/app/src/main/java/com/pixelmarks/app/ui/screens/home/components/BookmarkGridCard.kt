package com.pixelmarks.app.ui.screens.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pixelmarks.app.data.model.Bookmark
import com.pixelmarks.app.data.model.BookmarkCollection
import com.pixelmarks.app.ui.components.WebsiteThumbnail
import com.pixelmarks.app.ui.theme.ExpressiveMotion

@Composable
fun BookmarkGridCard(
    bookmark: Bookmark,
    collection: BookmarkCollection?,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefreshMetadata: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (isSelectionMode) {
                    onSelect()
                } else {
                    uriHandler.openUri(bookmark.url)
                }
            }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Clean Website Thumbnail with no middle logo
            Box {
                WebsiteThumbnail(
                    url = bookmark.url,
                    imageUrl = bookmark.imageUrl,
                    domain = bookmark.domain,
                    modifier = Modifier.fillMaxWidth()
                )

                // Favorite Star Button on top right
                if (!isSelectionMode) {
                    val scale by animateFloatAsState(
                        targetValue = if (bookmark.isFavorite) 1.15f else 1f,
                        animationSpec = ExpressiveMotion.bouncy,
                        label = "fav_scale"
                    )

                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(36.dp)
                            .scale(scale)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(percent = 50),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.85f),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (bookmark.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "Favorite",
                                    tint = if (bookmark.isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                } else if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Domain & Favicon line
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                if (bookmark.faviconUrl != null) {
                    AsyncImage(
                        model = bookmark.faviconUrl,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = bookmark.domain,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open in browser") },
                            onClick = {
                                menuExpanded = false
                                uriHandler.openUri(bookmark.url)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Refresh metadata & thumbnail") },
                            onClick = {
                                menuExpanded = false
                                onRefreshMetadata()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // Title
            Text(
                text = bookmark.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            // Description
            if (bookmark.description.isNotBlank()) {
                Text(
                    text = bookmark.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Tags
            if (bookmark.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    bookmark.tags.take(3).forEach { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text("#$tag", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(26.dp)
                        )
                    }
                }
            }
        }
    }
}

package com.pixelmarks.app.ui.screens.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelmarks.app.data.model.Bookmark
import com.pixelmarks.app.data.model.BookmarkCollection
import com.pixelmarks.app.ui.components.WebsiteThumbnail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkEditorSheet(
    bookmark: Bookmark?,
    initialUrl: String? = null,
    collections: List<BookmarkCollection>,
    onDismiss: () -> Unit,
    onSave: (url: String, title: String, description: String, collectionId: Long?, isFavorite: Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var url by remember { mutableStateOf(bookmark?.url ?: initialUrl ?: "") }
    var title by remember { mutableStateOf(bookmark?.title ?: "") }
    var description by remember { mutableStateOf(bookmark?.description ?: "") }
    var selectedCollectionId by remember { mutableStateOf(bookmark?.collectionId) }
    var isFavorite by remember { mutableStateOf(bookmark?.isFavorite ?: false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (bookmark != null) "Edit bookmark" else "Save a link",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live website thumbnail preview (clean, no middle logo)
            if (url.isNotBlank()) {
                WebsiteThumbnail(
                    url = url,
                    imageUrl = bookmark?.imageUrl,
                    domain = bookmark?.domain ?: "preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Link URL") },
                leadingIcon = { Icon(Icons.Rounded.Link, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Collection selection chips
            Text(
                text = "Collection",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedCollectionId == null,
                    onClick = { selectedCollectionId = null },
                    label = { Text("Unsorted") }
                )
                collections.forEach { col ->
                    FilterChip(
                        selected = selectedCollectionId == col.id,
                        onClick = { selectedCollectionId = col.id },
                        label = { Text(col.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Favorite switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to favorites", style = MaterialTheme.typography.bodyLarge)
                }
                Switch(checked = isFavorite, onCheckedChange = { isFavorite = it })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (bookmark != null && onDelete != null) {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                }

                Button(
                    onClick = {
                        if (url.isNotBlank()) {
                            onSave(url, title, description, selectedCollectionId, isFavorite)
                            onDismiss()
                        }
                    },
                    shape = RoundedCornerShape(percent = 50),
                    modifier = Modifier.weight(1f),
                    enabled = url.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

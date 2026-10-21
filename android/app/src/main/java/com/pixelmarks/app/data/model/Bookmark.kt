package com.pixelmarks.app.data.model

import java.time.Instant

data class Bookmark(
    val id: Long = 0,
    val url: String,
    val title: String,
    val description: String = "",
    val notes: String = "",
    val imageUrl: String? = null,
    val faviconUrl: String? = null,
    val domain: String = "",
    val collectionId: Long? = null,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val visitCount: Int = 0,
    val lastVisitedAt: Instant? = null,
    val remindAt: Instant? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

data class BookmarkCollection(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val color: String = "#6750A4",
    val icon: String = "folder",
    val shape: String = "cookie9",
    val position: Int = 0,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val count: Int = 0
)

data class TagCount(
    val name: String,
    val count: Int
)

data class WebsiteMetadata(
    val url: String,
    val domain: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val screenshotUrl: String?,
    val faviconUrl: String?,
    val siteName: String?
)

enum class SmartView {
    ALL, FAVORITES, REMINDERS, UNSORTED, ARCHIVE, COLLECTION
}

enum class SortOrder {
    RECENT, OLDEST, TITLE, VISITS
}

enum class ViewMode {
    GRID, LIST
}

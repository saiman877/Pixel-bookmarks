package com.pixelmarks.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "collections"
)
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val color: String = "#6750A4",
    val icon: String = "folder",
    val shape: String = "cookie9",
    val position: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant = Instant.now()
)

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("collection_id"),
        Index("created_at")
    ]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val title: String,
    val description: String = "",
    val notes: String = "",
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,
    @ColumnInfo(name = "favicon_url")
    val faviconUrl: String? = null,
    val domain: String = "",
    @ColumnInfo(name = "collection_id")
    val collectionId: Long? = null,
    val tags: List<String> = emptyList(),
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false,
    @ColumnInfo(name = "visit_count")
    val visitCount: Int = 0,
    @ColumnInfo(name = "last_visited_at")
    val lastVisitedAt: Instant? = null,
    @ColumnInfo(name = "remind_at")
    val remindAt: Instant? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant = Instant.now()
)

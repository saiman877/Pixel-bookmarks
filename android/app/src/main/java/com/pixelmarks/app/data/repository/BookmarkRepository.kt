package com.pixelmarks.app.data.repository

import com.pixelmarks.app.data.local.BookmarkDao
import com.pixelmarks.app.data.local.BookmarkEntity
import com.pixelmarks.app.data.local.CollectionDao
import com.pixelmarks.app.data.local.CollectionEntity
import com.pixelmarks.app.data.model.Bookmark
import com.pixelmarks.app.data.model.BookmarkCollection
import com.pixelmarks.app.data.remote.MetadataDetector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class BookmarkRepository(
    private val bookmarkDao: BookmarkDao,
    private val collectionDao: CollectionDao,
    private val metadataDetector: MetadataDetector = MetadataDetector()
) {
    val allBookmarks: Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks().map { list ->
        list.map { it.toDomain() }
    }

    val favorites: Flow<List<Bookmark>> = bookmarkDao.getFavorites().map { list ->
        list.map { it.toDomain() }
    }

    val reminders: Flow<List<Bookmark>> = bookmarkDao.getReminders().map { list ->
        list.map { it.toDomain() }
    }

    val unsorted: Flow<List<Bookmark>> = bookmarkDao.getUnsorted().map { list ->
        list.map { it.toDomain() }
    }

    val archive: Flow<List<Bookmark>> = bookmarkDao.getArchive().map { list ->
        list.map { it.toDomain() }
    }

    val collections: Flow<List<BookmarkCollection>> = collectionDao.getAllCollections().map { list ->
        list.map { it.toDomain() }
    }

    fun getBookmarksByCollection(collectionId: Long): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksByCollection(collectionId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun saveBookmark(
        url: String,
        title: String? = null,
        description: String? = null,
        collectionId: Long? = null,
        tags: List<String> = emptyList(),
        isFavorite: Boolean = false,
        fetchMetadata: Boolean = true
    ): Bookmark {
        val meta = if (fetchMetadata || title.isNullOrBlank()) {
            metadataDetector.detect(url)
        } else null

        val entity = BookmarkEntity(
            url = meta?.url ?: url,
            title = title?.ifBlank { null } ?: meta?.title ?: url,
            description = description ?: meta?.description ?: "",
            imageUrl = meta?.imageUrl,
            faviconUrl = meta?.faviconUrl,
            domain = meta?.domain ?: "",
            collectionId = collectionId,
            tags = tags,
            isFavorite = isFavorite
        )

        val id = bookmarkDao.insert(entity)
        return entity.copy(id = id).toDomain()
    }

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.update(bookmark.toEntity())
    }

    suspend fun toggleFavorite(bookmark: Bookmark) {
        bookmarkDao.updateFavorite(listOf(bookmark.id), !bookmark.isFavorite)
    }

    suspend fun toggleArchive(bookmark: Bookmark) {
        bookmarkDao.updateArchive(listOf(bookmark.id), !bookmark.isArchived)
    }

    suspend fun deleteBookmarks(ids: List<Long>) {
        bookmarkDao.deleteByIds(ids)
    }

    suspend fun moveBookmarks(ids: List<Long>, collectionId: Long?) {
        bookmarkDao.updateCollection(ids, collectionId)
    }

    suspend fun recordVisit(id: Long) {
        bookmarkDao.recordVisit(id)
    }

    suspend fun refreshMetadata(id: Long): Bookmark? {
        val entity = bookmarkDao.getById(id) ?: return null
        val meta = metadataDetector.detect(entity.url)
        val updated = entity.copy(
            title = meta.title,
            description = meta.description,
            imageUrl = meta.imageUrl ?: entity.imageUrl,
            faviconUrl = meta.faviconUrl ?: entity.faviconUrl,
            updatedAt = Instant.now()
        )
        bookmarkDao.update(updated)
        return updated.toDomain()
    }

    suspend fun createCollection(name: String, color: String, icon: String, shape: String, description: String = ""): Long {
        return collectionDao.insert(
            CollectionEntity(
                name = name,
                description = description,
                color = color,
                icon = icon,
                shape = shape
            )
        )
    }

    suspend fun deleteCollection(collection: BookmarkCollection) {
        collectionDao.delete(collection.toEntity())
    }

    private fun BookmarkEntity.toDomain() = Bookmark(
        id = id,
        url = url,
        title = title,
        description = description,
        notes = notes,
        imageUrl = imageUrl,
        faviconUrl = faviconUrl,
        domain = domain,
        collectionId = collectionId,
        tags = tags,
        isFavorite = isFavorite,
        isArchived = isArchived,
        visitCount = visitCount,
        lastVisitedAt = lastVisitedAt,
        remindAt = remindAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Bookmark.toEntity() = BookmarkEntity(
        id = id,
        url = url,
        title = title,
        description = description,
        notes = notes,
        imageUrl = imageUrl,
        faviconUrl = faviconUrl,
        domain = domain,
        collectionId = collectionId,
        tags = tags,
        isFavorite = isFavorite,
        isArchived = isArchived,
        visitCount = visitCount,
        lastVisitedAt = lastVisitedAt,
        remindAt = remindAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun CollectionEntity.toDomain() = BookmarkCollection(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        shape = shape,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun BookmarkCollection.toEntity() = CollectionEntity(
        id = id,
        name = name,
        description = description,
        color = color,
        icon = icon,
        shape = shape,
        position = position,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

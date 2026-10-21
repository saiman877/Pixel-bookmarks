package com.pixelmarks.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE is_archived = 0 ORDER BY created_at DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE is_favorite = 1 AND is_archived = 0 ORDER BY created_at DESC")
    fun getFavorites(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE remind_at IS NOT NULL AND is_archived = 0 ORDER BY remind_at ASC")
    fun getReminders(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE collection_id IS NULL AND is_archived = 0 ORDER BY created_at DESC")
    fun getUnsorted(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE is_archived = 1 ORDER BY updated_at DESC")
    fun getArchive(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE collection_id = :collectionId AND is_archived = 0 ORDER BY created_at DESC")
    fun getBookmarksByCollection(collectionId: Long): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): BookmarkEntity?

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun getByUrl(url: String): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookmarks: List<BookmarkEntity>): List<Long>

    @Update
    suspend fun update(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("UPDATE bookmarks SET is_favorite = :favorite, updated_at = :now WHERE id IN (:ids)")
    suspend fun updateFavorite(ids: List<Long>, favorite: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE bookmarks SET is_archived = :archived, updated_at = :now WHERE id IN (:ids)")
    suspend fun updateArchive(ids: List<Long>, archived: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE bookmarks SET collection_id = :collectionId, updated_at = :now WHERE id IN (:ids)")
    suspend fun updateCollection(ids: List<Long>, collectionId: Long?, now: Long = System.currentTimeMillis())

    @Query("UPDATE bookmarks SET visit_count = visit_count + 1, last_visited_at = :now WHERE id = :id")
    suspend fun recordVisit(id: Long, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM bookmarks WHERE is_archived = 0")
    fun getActiveCount(): Flow<Int>
}

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY position ASC, id ASC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(collection: CollectionEntity): Long

    @Update
    suspend fun update(collection: CollectionEntity)

    @Delete
    suspend fun delete(collection: CollectionEntity)

    @Query("SELECT COUNT(*) FROM bookmarks WHERE collection_id = :collectionId AND is_archived = 0")
    fun getBookmarkCountForCollection(collectionId: Long): Flow<Int>
}

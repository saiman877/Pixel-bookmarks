package com.pixelmarks.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [BookmarkEntity::class, CollectionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun collectionDao(): CollectionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pixelmarks_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.collectionDao(), database.bookmarkDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(collectionDao: CollectionDao, bookmarkDao: BookmarkDao) {
            val designId = collectionDao.insert(
                CollectionEntity(
                    name = "Design inspiration",
                    description = "Systems, type & color that spark joy",
                    color = "#E52592",
                    icon = "palette",
                    shape = "flower",
                    position = 0
                )
            )
            val devId = collectionDao.insert(
                CollectionEntity(
                    name = "Dev toolbox",
                    description = "Docs and tools I reach for daily",
                    color = "#1A73E8",
                    icon = "code",
                    shape = "cookie9",
                    position = 1
                )
            )
            val readId = collectionDao.insert(
                CollectionEntity(
                    name = "Read later",
                    description = "Long reads for slow evenings",
                    color = "#F9AB00",
                    icon = "menu_book",
                    shape = "sunny",
                    position = 2
                )
            )

            bookmarkDao.insert(
                BookmarkEntity(
                    url = "https://m3.material.io/",
                    title = "Material Design 3",
                    description = "Google's open-source design system — expressive components, dynamic color, shape and motion.",
                    imageUrl = "https://m3.material.io/static/assets/m3-social.jpg",
                    domain = "m3.material.io",
                    collectionId = designId,
                    tags = listOf("design", "material"),
                    isFavorite = true,
                    visitCount = 42
                )
            )
            bookmarkDao.insert(
                BookmarkEntity(
                    url = "https://developer.android.com/develop/ui/compose",
                    title = "Jetpack Compose",
                    description = "Android's modern toolkit for building native UI.",
                    imageUrl = "https://s0.wp.com/mshots/v1/https%3A%2F%2Fdeveloper.android.com%2Fdevelop%2Fui%2Fcompose?w=960",
                    domain = "developer.android.com",
                    collectionId = devId,
                    tags = listOf("android", "compose", "kotlin"),
                    isFavorite = true,
                    visitCount = 57
                )
            )
            bookmarkDao.insert(
                BookmarkEntity(
                    url = "https://blog.google/products/android/material-3-expressive-android-wearos-launch/",
                    title = "Android and Wear OS are getting a big refresh",
                    description = "Material 3 Expressive brings springy motion, new shapes and bolder type to Android.",
                    imageUrl = "https://images.pexels.com/photos/12732215/pexels-photo-12732215.jpeg?auto=compress&cs=tinysrgb&fit=crop&h=500&w=800",
                    domain = "blog.google",
                    collectionId = readId,
                    tags = listOf("android", "material"),
                    isFavorite = false,
                    visitCount = 14
                )
            )
        }
    }
}

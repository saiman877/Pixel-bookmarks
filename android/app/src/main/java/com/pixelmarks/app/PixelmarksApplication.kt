package com.pixelmarks.app

import android.app.Application
import com.pixelmarks.app.data.local.AppDatabase
import com.pixelmarks.app.data.repository.BookmarkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PixelmarksApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { BookmarkRepository(database.bookmarkDao(), database.collectionDao()) }
}

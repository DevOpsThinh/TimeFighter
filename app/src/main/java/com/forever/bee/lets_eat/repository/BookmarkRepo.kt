/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.forever.bee.lets_eat.db.BookmarkDao
import com.forever.bee.lets_eat.db.PlaceBookDB
import com.forever.bee.lets_eat.model.Bookmark

class BookmarkRepo(private val context: Context) {
    private val db = PlaceBookDB.getInstance(context)
    private val bookmarkDao: BookmarkDao = db.bookmarkDao()

    fun getLiveBookmark(bookmarkId: Long): LiveData<Bookmark> = bookmarkDao.loadLiveBookmark(bookmarkId)

    fun addBookmark(bookmark: Bookmark): Long? {
        val newId = bookmarkDao.insertBookmark(bookmark)
        bookmark.id = newId
        return newId
    }

    fun createBookmark(): Bookmark {
        return Bookmark()
    }

    val allBookmarks: LiveData<List<Bookmark>>
        get() {
            return bookmarkDao.loadAll()
        }
}
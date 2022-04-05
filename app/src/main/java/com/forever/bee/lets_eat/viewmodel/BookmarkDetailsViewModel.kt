/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.forever.bee.lets_eat.model.Bookmark
import com.forever.bee.lets_eat.repository.BookmarkRepo
import com.forever.bee.lets_eat.util.ImageUtils

class BookmarkDetailsViewModel(application: Application): AndroidViewModel(application) {
    private var bookmarkDetailsView: LiveData<BookmarkDetailsView>? = null
    private val bookmarkRepo = BookmarkRepo(getApplication())

    fun getBookmark(bookmarId: Long): LiveData<BookmarkDetailsView>? {
        if (bookmarkDetailsView == null) {
            mapBookmarkToBookmarkView(bookmarId)
        }
        return bookmarkDetailsView
    }

    private fun mapBookmarkToBookmarkView(bookmarkId: Long) {
        val bookmark = bookmarkRepo.getLiveBookmark(bookmarkId)
        bookmarkDetailsView = Transformations.map(bookmark) {
            repoBookmark ->
            bookmarkToBookmarkView(repoBookmark)
        }
    }

    private fun bookmarkToBookmarkView(bookmark: Bookmark): BookmarkDetailsView {
        return BookmarkDetailsView(
            bookmark.id,
            bookmark.name,
            bookmark.phone,
            bookmark.address,
            bookmark.notes
        )
    }

    data class BookmarkDetailsView(
        var id: Long? = null,
        var name: String = "",
        var phone: String = "",
        var address: String = "",
        var notes: String = ""
    ) {
        fun getImage(context: Context) = id?.let {
            ImageUtils.loadBitmapFromFile(context, Bookmark.generateImageFilename(it))
        }
    }
}
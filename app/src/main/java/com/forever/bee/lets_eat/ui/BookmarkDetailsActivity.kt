/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.forever.bee.lets_eat.R
import com.forever.bee.lets_eat.databinding.ActivityBookmarkDetailsBinding
import com.forever.bee.lets_eat.viewmodel.BookmarkDetailsViewModel

class BookmarkDetailsActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityBookmarkDetailsBinding

    /**
     * The standard procedure for initializing a view model.
     * */
    private val bookmarkDetailsViewModel by viewModels<BookmarkDetailsViewModel>()
    private var bookmarkDetailsView: BookmarkDetailsViewModel.BookmarkDetailsView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark_details)

        setupToolbar()
        getIntentData()
    }

    /**
     * Inflates the menu resource (action save menu).
     * */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bookmark_details, menu)
        return true
    }

    /**
     * Respond to the user tapping the checkmark menu item.
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            saveBookmarkChanges()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Takes the current changes from the edit text fields  & updates the bookmark.
     * */
    private fun saveBookmarkChanges() {
        val name = dataBinding.editTextName.text.toString()

        if (name.isEmpty()) {
            return
        }
        bookmarkDetailsView?.let { bookmarkView ->
            bookmarkView.name = dataBinding.editTextName.text.toString()
            bookmarkView.phone = dataBinding.editTextPhone.text.toString()
            bookmarkView.address = dataBinding.editTextAddress.text.toString()
            bookmarkView.notes = dataBinding.editTextNotes.text.toString()

            bookmarkDetailsViewModel.updateBookmark(bookmarkView)
        }
        finish()
    }

    /**
     * Reads this Intent data & use it to populate the UI.
     * */
    private fun getIntentData() {
        val bookmarkId = intent.getLongExtra(
            MapsActivity.Companion.EXTRA_BOOKMARK_ID, 0
        )
        bookmarkDetailsViewModel.getBookmark(bookmarkId)?.observe(this) {
            it?.let {
                bookmarkDetailsView = it
                dataBinding.bookmarkDetailsView = it
                populateImageView()
            }
        }
    }

    /**
     * Loads the image from bookmarkView & then uses it to set the imageViewPlace.
     * */
    private fun populateImageView() {
        bookmarkDetailsView?.let { bookmarkView ->
            val placeImage = bookmarkView.getImage(this)
            placeImage?.let {
                dataBinding.imageViewPlace.setImageBitmap(placeImage)
            }
        }
    }

    /**
     * Makes the Toolbar act as the ActionBar for [BookmarkDetailsActivity].
     * */
    private fun setupToolbar() {
        setSupportActionBar(dataBinding.toolbar)
    }
}
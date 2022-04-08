/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.forever.bee.lets_eat.R
import com.forever.bee.lets_eat.databinding.ActivityBookmarkDetailsBinding
import com.forever.bee.lets_eat.util.ImageUtils
import com.forever.bee.lets_eat.viewmodel.BookmarkDetailsViewModel
import java.io.File
import java.net.URLEncoder

class BookmarkDetailsActivity : AppCompatActivity(), PhotoOptionDialogFragment.PhotoOptionDialogListener {

    companion object {
        private const val REQUEST_CAPTURE_IMAGE = 1
        private const val REQUEST_GALLERY_IMAGE = 2
    }

    private lateinit var dataBinding: ActivityBookmarkDetailsBinding

    private var photoFile: File? = null

    /**
     * The standard procedure for initializing a view model.
     * */
    private val bookmarkDetailsViewModel by viewModels<BookmarkDetailsViewModel>()
    private var bookmarkDetailsView: BookmarkDetailsViewModel.BookmarkDetailsView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark_details)

        setupToolbar()
        setupFabShare()
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
        R.id.action_delete -> {
            deleteBookmark()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * This method is called by Android when an Activity returns a result such as the Camera capture activity.
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == android.app.Activity.RESULT_OK) {
            when (resultCode) {
                REQUEST_CAPTURE_IMAGE -> {
                    val photoFile = photoFile ?: return
                    val uri = FileProvider.getUriForFile(this, "com.forever.bee.lets_eat.fileprovider", photoFile)
                    revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    val image = getImageWithPath(photoFile.absolutePath)
                    val bitmap = ImageUtils.rotateImageIfRequired(this, image, uri)
                    updateImage(bitmap)
                }
                REQUEST_GALLERY_IMAGE -> if (data != null && data.data != null) {
                    val imageUri = data.data as Uri
                    val image = getImageWithAuthority(imageUri)

                    image?.let {
                        val bitmap = ImageUtils.rotateImageIfRequired(this, it, imageUri)
                        updateImage(bitmap)
                    }
                }
            }
        }
    }

    private fun sharePlace() {
        val booView = bookmarkDetailsView ?: return

        var mMapUrl = ""

        if (booView.placeId == null) {
            val location = URLEncoder.encode("${booView.latitude}," + "${booView.longitude}", "utf-8")
            mMapUrl = "https://www.google.com/maps/dir/?api=1&destination=$location"
        } else {
            val name = URLEncoder.encode(booView.name, "utf-8")
            mMapUrl = "https://www.google.com/maps/dir/?api=1&destination=$name&destination_place_id=" +
                    "${booView.placeId}"
        }

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out ${booView.name} at:\n$mMapUrl")
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing ${booView.name}")
        sendIntent.type = "text/plain"

        startActivity(sendIntent)
    }

    /**
     * Displays a standard AlertDialog to ask users if they want to delete the bookmark.
     * */
    private fun deleteBookmark() {
        val booView = bookmarkDetailsView ?: return

        AlertDialog.Builder(this)
            .setMessage(getString(R.string.action_delete))
            .setPositiveButton(getString(R.string.option_ok)) { _, _ ->
                bookmarkDetailsViewModel.deleteBookmark(booView)
                finish()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()
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
            bookmarkView.category = dataBinding.spinnerCategory.selectedItem as String
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
                populateCategoryList()
            }
        }
    }

    /**
     * This is a standard way to populate a Spinner control in Android. In that case, it is the categories list spinner.
     * */
    private fun populateCategoryList() {
        val bookmarkView = bookmarkDetailsView ?: return
        val resourceId = bookmarkDetailsViewModel.getCategoryResourceId(bookmarkView.category)

        resourceId?.let {
            dataBinding.imageViewCategory.setImageResource(it)
        }

        val categories = bookmarkDetailsViewModel.getCategories()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dataBinding.spinnerCategory.adapter = adapter

        val plCategory = bookmarkView.category
        dataBinding.spinnerCategory.setSelection(adapter.getPosition(plCategory))

        dataBinding.spinnerCategory.post {
            dataBinding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val cate = p0?.getItemAtPosition(p2) as String
                    val resourceId = bookmarkDetailsViewModel.getCategoryResourceId(cate)

                    resourceId?.let {
                        dataBinding.imageViewCategory.setImageResource(it)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // is required but not used.
                }
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

        dataBinding.imageViewPlace.setOnClickListener {
            replaceImage()
        }
    }

    /**
     * Makes the Toolbar act as the ActionBar for [BookmarkDetailsActivity].
     * */
    private fun setupToolbar() {
        setSupportActionBar(dataBinding.toolbar)
    }

    private fun setupFabShare() {
        dataBinding.fabShare.setOnClickListener {
            sharePlace()
        }
    }

    private fun getImageWithAuthority(uri: Uri) = ImageUtils.decodeUriStreamToSize(
        uri,
        resources.getDimensionPixelSize(R.dimen.default_image_width),
        resources.getDimensionPixelSize(R.dimen.default_image_height),
        this
    )

    private fun getImageWithPath(filePath: String) = ImageUtils.decodeFileToSize(
        filePath,
        resources.getDimensionPixelSize(R.dimen.default_image_width),
        resources.getDimensionPixelSize(R.dimen.default_image_height)
    )

    private fun updateImage(image: Bitmap) {
        bookmarkDetailsView?.let {
            dataBinding.imageViewPlace.setImageBitmap(image)
            it.setImage(this, image)
        }
    }

    private fun replaceImage() {
        val newFragment = PhotoOptionDialogFragment.newInstance(this)
        newFragment?.show(supportFragmentManager, "photoOptionDialog")
    }

    override fun onCaptureClick() {
        photoFile = null

        try {
            photoFile = ImageUtils.createUniqueImageFile(this)
        } catch (ex: java.io.IOException) {
            return
        }

        photoFile?.let { photoFile ->
            val photoUri = FileProvider.getUriForFile(this, "com.forever.bee.lets_eat.fileprovider", photoFile)

            val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)

            val intentActivities =
                packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
            intentActivities.map { it.activityInfo.packageName }.forEach {
                grantUriPermission(it, photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            startActivityForResult(captureIntent, REQUEST_CAPTURE_IMAGE)
        }

//        Toast.makeText(this, getString(R.string.camera_capture), Toast.LENGTH_SHORT).show()
    }

    override fun onPickClick() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, REQUEST_GALLERY_IMAGE)
//        Toast.makeText(this, getString(R.string.gallery_pick), Toast.LENGTH_SHORT).show()
    }
}
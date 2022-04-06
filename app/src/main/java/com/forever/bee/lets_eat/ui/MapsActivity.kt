/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.forever.bee.lets_eat.BuildConfig
import com.forever.bee.lets_eat.R
import com.forever.bee.lets_eat.adapter.BookmarkInfoAdapter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.forever.bee.lets_eat.databinding.ActivityMapsBinding
import com.forever.bee.lets_eat.viewmodel.MapsViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    private val mapsViewModel by viewModels<MapsViewModel>()

    companion object {
        const val EXTRA_BOOKMARK_ID = "com.forever.bee.lets_eat.EXTRA_BOOKMARK_ID"
        private const val REQUEST_LOCATION = 1
        private const val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupLocationClient()
        setupPlacesClient()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setupMapListeners()
        createBookmarkMarkerObserver()
        getCurrentLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Log.e(TAG, "Location permission denied")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.about_item -> {
            showInfo()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun createBookmarkMarkerObserver() {
        mapsViewModel.getBookmarkMarkerViews()?.observe(
            this
        ) {
            mMap.clear()

            it?.let {
                displayAllBookmarks(it)
            }
        }
    }

    private fun displayAllBookmarks(it: List<MapsViewModel.BookmarkMarkerView>) {
        it.forEach {
            addPlaceMarker(it)
        }
    }

    private fun addPlaceMarker(it: MapsViewModel.BookmarkMarkerView): Marker? {
        val marker = mMap.addMarker(MarkerOptions()
            .position(it.location)
            .title(it.name)
            .snippet(it.phone)
            .icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE
            ))
            .alpha(0.8f))

        if (marker != null) {
            marker.tag = it
        }

        return marker
    }

    private fun startBookmarkDetails(bookmarkId: Long) {
        val intent = Intent(this,BookmarkDetailsActivity::class.java)
        intent.putExtra(EXTRA_BOOKMARK_ID, bookmarkId)
        startActivity(intent)
    }
    /**
     * Handles the action when a user taps a place Info Window
     * */
    private fun handleInfoWindowClick(marker: Marker) {
        when (marker.tag) {
            is PlaceInfo -> {
                val placeInfo = (marker.tag as PlaceInfo)
                if (placeInfo.place != null && placeInfo.image != null) {
                    GlobalScope.launch {
                        mapsViewModel.addBookmarkFromPlace(placeInfo.place, placeInfo.image)
                    }
                }
                marker.remove()
            }
            is MapsViewModel.BookmarkMarkerView -> {
                val bookmarkMarkerView = (marker.tag as MapsViewModel.BookmarkMarkerView)
                marker.hideInfoWindow()
                bookmarkMarkerView.id?.let {
                    startBookmarkDetails(it)
                }
            }
        }

    }

    private fun setupMapListeners() {
        mMap.setInfoWindowAdapter(BookmarkInfoAdapter(this))
        mMap.setOnPoiClickListener {
            displayPOI(it)
        }
        mMap.setOnInfoWindowClickListener {
            handleInfoWindowClick(it)
        }
    }

    private fun displayPOIDisplayStep(place: Place, photo: Bitmap?) {
//        val iconPhoto = if (photo == null) {
//            BitmapDescriptorFactory.defaultMarker()
//        } else {
//            BitmapDescriptorFactory.fromBitmap(photo)
//        }

//        mMap.addMarker(MarkerOptions()
//            .position(place.latLng as LatLng)
////            .icon(iconPhoto)
//            .title(place.name)
//            .snippet(place.phoneNumber))

        val marker = mMap.addMarker(
            MarkerOptions()
                .position(place.latLng as LatLng)
                .title(place.name)
                .snippet(place.phoneNumber)
        )
        marker?.tag = PlaceInfo(place, photo)
        marker?.showInfoWindow()
    }

    private fun displayPOIGetPhoto(place: Place) {
        val photoMetadata = place.photoMetadatas?.get(0)

        if (photoMetadata == null) {
            displayPOIDisplayStep(place, null)
            return
        }

        val photoRequest = FetchPhotoRequest.builder(photoMetadata)
            .setMaxWidth(
                resources.getDimensionPixelSize(
                    R.dimen.default_image_width
                )
            )
            .setMaxHeight(
                resources.getDimensionPixelSize(
                    R.dimen.default_image_height
                )
            ).build()

        placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
            val bitmap = fetchPhotoResponse.bitmap
            displayPOIDisplayStep(place, bitmap)
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                Log.e(TAG, "Place not found: " + exception.message + ", " + "statusCode: " + statusCode)
            }
        }
    }

    private fun displayPOI(pointOfInterest: PointOfInterest) {
        displayPOIStep(pointOfInterest)
    }

    private fun displayPOIStep(pointOfInterest: PointOfInterest) {
        val placeId = pointOfInterest.placeId

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
//            Toast.makeText(this, "${place.name}, " + "${place.phoneNumber}", Toast.LENGTH_LONG).show()
            displayPOIGetPhoto(place)
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
                Log.e(TAG, "Place not found: " + exception.message + ", " + "statusCode: " + statusCode)
            }
        }
    }

    private fun setupPlacesClient() {
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions()
        } else {
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                val location = it.result
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f)
                    mMap.moveCamera(update)
                } else {
                    Log.e(TAG, "No location found.")
                }
            }
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION
        )
    }

    private fun setupLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    class PlaceInfo(
        val place: Place? = null,
        val image: Bitmap? = null
    )
}
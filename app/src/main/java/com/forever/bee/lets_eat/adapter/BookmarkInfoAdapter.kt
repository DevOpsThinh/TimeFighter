package com.forever.bee.lets_eat.adapter

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import com.forever.bee.lets_eat.databinding.ContentBookmarkInfoBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class BookmarkInfoAdapter(context: Activity) : GoogleMap.InfoWindowAdapter {

    private val binding = ContentBookmarkInfoBinding.inflate(context.layoutInflater)

    override fun getInfoContents(p0: Marker): View? {
        binding.title.text = p0.title ?: ""
        binding.phone.text = p0.snippet ?: ""
        val imageView = binding.photo
        imageView.setImageBitmap((p0.tag as Bitmap))
        return binding.root
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}
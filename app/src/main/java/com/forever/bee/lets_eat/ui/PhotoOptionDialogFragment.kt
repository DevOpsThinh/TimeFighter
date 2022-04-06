/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.forever.bee.lets_eat.R

class PhotoOptionDialogFragment : DialogFragment() {

    interface PhotoOptionDialogListener {
        fun onCaptureClick()
        fun onPickClick()
    }

    companion object {
        fun canPick(context: Context): Boolean {
            val picIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            return (picIntent.resolveActivity(
                context.packageManager
            ) != null
                    )
        }

        fun canCapture(context: Context): Boolean {
            val captureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )

            return (captureIntent.resolveActivity(
                context.packageManager
            ) != null)
        }

        fun newInstance(context: Context) =
            if (canPick(context) || canCapture(context)) {
                PhotoOptionDialogFragment()
            } else {
                null
            }
    }

    private lateinit var listener: PhotoOptionDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        listener = activity as PhotoOptionDialogListener

        var captureSelectIdx = -1
        var pickSelectIdx = -1

        val options = ArrayList<String>()
        val context = activity as Context

        if (canCapture(context)) {
            options.add(getString(R.string.camera))
            captureSelectIdx = 0
        }

        if (canPick(context)) {
            options.add(getString(R.string.gallery))
            pickSelectIdx = if (captureSelectIdx == 0) 1 else 0
        }

        return AlertDialog.Builder(context)
            .setTitle(getString(R.string.photo_option))
            .setItems(options.toTypedArray<CharSequence>()) { _, which ->
                if (which == captureSelectIdx) {
                    listener.onCaptureClick()
                } else if (which == pickSelectIdx) {
                    listener.onPickClick()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
    }
}
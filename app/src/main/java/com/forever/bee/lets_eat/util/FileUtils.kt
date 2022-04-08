/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.util

import android.content.Context
import java.io.File

object FileUtils {
    fun deleteFile(context: Context, fileName: String) {
        val dir = context.filesDir
        val file = File(dir, fileName)
        file.delete()
    }
}
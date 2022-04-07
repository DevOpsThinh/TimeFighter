/**
 * Android Programming With Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6 - JDK 11 (Java 11)
 *
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.lets_eat.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.forever.bee.lets_eat.model.Bookmark

@Database(entities = [Bookmark::class], version = 3)
abstract class PlaceBookDB : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private var instance: PlaceBookDB? = null

        fun getInstance(context: Context): PlaceBookDB {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext, PlaceBookDB::class.java, "PlaceBook")
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance as PlaceBookDB
        }
    }
}
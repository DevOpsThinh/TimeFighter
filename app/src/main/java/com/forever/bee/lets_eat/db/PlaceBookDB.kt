package com.forever.bee.lets_eat.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.forever.bee.lets_eat.model.Bookmark

@Database(entities = [Bookmark::class], version = 1)
abstract class PlaceBookDB : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private var instance: PlaceBookDB? = null

        fun getInstance(context: Context): PlaceBookDB {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext, PlaceBookDB::class.java, "PlaceBook").build()
            }

            return instance as PlaceBookDB
        }
    }
}
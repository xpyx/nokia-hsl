package com.xpyx.nokiahslvisualisation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xpyx.nokiahslvisualisation.utils.Converters

@Database(entities = arrayOf(StopTimesItem::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StopTimesDatabase : RoomDatabase() {

    // Get DAO
    abstract fun stopTimesDao(): StopTimesDao

    companion object {
        @Volatile
        private var INSTANCE: StopTimesDatabase? = null

        fun getDatabase(context: Context): StopTimesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StopTimesDatabase::class.java,
                    "stop_times_item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

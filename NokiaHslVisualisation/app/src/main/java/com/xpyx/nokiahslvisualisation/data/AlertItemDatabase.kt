package com.xpyx.nokiahslvisualisation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xpyx.nokiahslvisualisation.utils.Converters

@Database(entities = arrayOf(AlertItem::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AlertItemDatabase : RoomDatabase() {

    // Get DAO
    abstract fun alertDao(): AlertItemDao

    companion object {
        @Volatile
        private var INSTANCE: AlertItemDatabase? = null

        fun getDatabase(context: Context): AlertItemDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlertItemDatabase::class.java,
                    "alert_item_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

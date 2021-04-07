package com.xpyx.nokiahslvisualisation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xpyx.nokiahslvisualisation.utils.Converters

@Database(entities = arrayOf(DataTrafficItem::class), version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TrafficItemDatabase : RoomDatabase() {

    // Get DAO
    abstract fun trafficDao(): TrafficItemDao

    companion object {
        @Volatile
        private var INSTANCE: TrafficItemDatabase? = null

        fun getDatabase(context: Context): TrafficItemDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrafficItemDatabase::class.java,
                    "traffic_item_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

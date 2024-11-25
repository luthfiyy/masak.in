package com.upi.masakin.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.upi.masakin.data.entities.Chef

@Database(entities = [Chef::class], version = 1, exportSchema = false)
abstract class MasakinDatabase : RoomDatabase() {
    abstract fun ChefDao(): ChefDao

    companion object {
        @Volatile
        private var INSTANCE: MasakinDatabase? = null

        fun getDatabase(context: Context): MasakinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MasakinDatabase::class.java,
                    "masakin_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.upi.masakin.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.dao.RecipeDao
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.utils.RecipeConverters

@Database(
    entities = [Chef::class, RecipeEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(RecipeConverters::class)
abstract class MasakinDatabase : RoomDatabase() {
    abstract fun chefDao(): ChefDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: MasakinDatabase? = null

        fun getDatabase(context: Context): MasakinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MasakinDatabase::class.java,
                    "masakin_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


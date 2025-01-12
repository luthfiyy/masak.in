package com.upi.masakin.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.upi.masakin.data.dao.ArticleDao
import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.dao.RecipeDao
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.data.entities.Article
import com.upi.masakin.utils.RecipeConverters

@Database(
    entities = [Chef::class, RecipeEntity::class, Article::class],
    version = 23,
    exportSchema = false
)

@TypeConverters(RecipeConverters::class)
abstract class MasakinDatabase : RoomDatabase() {
    abstract fun chefDao(): ChefDao
    abstract fun recipeDao(): RecipeDao
    abstract fun articleDao(): ArticleDao

}


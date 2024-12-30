package com.upi.masakin.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.upi.masakin.data.dao.ArticleDao
import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.dao.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMasakinDatabase(@ApplicationContext context: Context): MasakinDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MasakinDatabase::class.java,
            "masakin_database"
        )
            .createFromAsset("masakin_database.db")
            .fallbackToDestructiveMigration()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @Provides
    @Singleton
    fun provideChefDao(database: MasakinDatabase): ChefDao {
        return database.chefDao()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: MasakinDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: MasakinDatabase): ArticleDao {
        return database.articleDao()
    }

}


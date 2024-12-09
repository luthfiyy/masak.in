package com.upi.masakin.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.upi.masakin.data.dao.ArticleDao
import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.dao.RecipeDao
import com.upi.masakin.data.repository.ChefRepository
import com.upi.masakin.data.repository.ChefRepositoryImpl
import com.upi.masakin.data.repository.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE) // Optional: For performance
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

    @Provides
    @Singleton
    fun provideChefRepository(chefDao: ChefDao): ChefRepository {
        return ChefRepositoryImpl(chefDao)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        @ApplicationContext context: Context,
        masakinDatabase: MasakinDatabase,
        ioDispatcher: CoroutineDispatcher
    ): RecipeRepository {
        return RecipeRepository(context, masakinDatabase, ioDispatcher)
    }


    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
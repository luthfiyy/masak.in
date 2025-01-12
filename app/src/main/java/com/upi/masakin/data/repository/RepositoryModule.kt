package com.upi.masakin.data.repository

import android.content.Context
import com.upi.masakin.data.api.recipe.MealApiService
import com.upi.masakin.data.dao.ChefDao
import com.upi.masakin.data.repository.chef.ChefRepository
import com.upi.masakin.data.repository.chef.ChefRepositoryImpl
import com.upi.masakin.data.repository.recipe.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChefRepository(chefDao: ChefDao): ChefRepository {
        return ChefRepositoryImpl(chefDao)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        @ApplicationContext context: Context,
        mealApiService: MealApiService,
        ioDispatcher: CoroutineDispatcher
    ): RecipeRepository {
        return RecipeRepository(context, ioDispatcher, mealApiService)
    }
}
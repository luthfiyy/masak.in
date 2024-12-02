package com.upi.masakin.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upi.masakin.data.entities.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article_table")
    fun getAllArticle(): Flow<List<Article>>

    @Query("SELECT * FROM article_table")
    suspend fun getAllArticleSync(): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(recipes: List<Article>)

    @Query("SELECT COUNT(*) FROM article_table")
    suspend fun getArticleCount(): Int
}
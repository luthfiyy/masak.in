package com.upi.masakin.data.repository

import com.upi.masakin.data.dao.ArticleDao
import com.upi.masakin.data.entities.Article
import com.upi.masakin.data.entities.sampleArticles

class ArticleRepository(private val articleDao: ArticleDao) {

    suspend fun populateInitialArticle() {
        if (articleDao.getArticleCount() == 0) {
            articleDao.insertArticle(sampleArticles)
        }
    }

    suspend fun getAllArticle(): List<Article> {
        return articleDao.getAllArticleSync()
    }
}

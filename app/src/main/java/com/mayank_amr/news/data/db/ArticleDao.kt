package com.mayank_amr.news.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.mayank_amr.news.data.response.HeadlinesResponse
import kotlinx.coroutines.flow.Flow

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 10:24 AM
 */

/**
 * This is Data Access object for Headline(Article)
 */

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<HeadlinesResponse.Article>>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY updatedAt")
    fun getArticlesPage(category: String): PagingSource<Int, HeadlinesResponse.Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<HeadlinesResponse.Article>)

    @Query("DELETE FROM articles WHERE category = :category")
    suspend fun deleteAllArticles(category: String)

    @Update
    suspend fun updateArticle(article: HeadlinesResponse.Article)

}
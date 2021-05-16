package com.mayank_amr.news.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.mayank_amr.news.data.response.HeadlinesResponse
import kotlinx.coroutines.flow.Flow

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 10:24 AM
 */

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<HeadlinesResponse.Article>>

    @Query("SELECT * FROM articles")
    fun getArticlesPage(): PagingSource<Int, HeadlinesResponse.Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<HeadlinesResponse.Article>)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Update
    suspend fun updateArticle(article: HeadlinesResponse.Article)

}
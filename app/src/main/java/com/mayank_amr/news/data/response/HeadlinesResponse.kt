package com.mayank_amr.news.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey

data class HeadlinesResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
) {
    @Entity(tableName = "articles")
    data class Article(
        val author: String?,
        val content: String?,
        val description: String?,
        val publishedAt: String,
        //val source: Source?,
        @PrimaryKey val title: String,
        val url: String?,
        val urlToImage: String?,
        val isFavourite: Boolean,
        val updatedAt: Long = System.currentTimeMillis()
    ) {
        data class Source(
            val id: String?,
            val name: String?
        )
    }
}
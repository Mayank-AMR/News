package com.mayank_amr.news.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mayank_amr.news.data.db.paging.RemoteKeys
import com.mayank_amr.news.data.db.paging.RemoteKeysDao
import com.mayank_amr.news.data.response.HeadlinesResponse

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 10:38 AM
 */

@Database(entities = [HeadlinesResponse.Article::class,RemoteKeys::class], version = 1)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getRepoDao(): RemoteKeysDao
    abstract fun articleDao(): ArticleDao

    companion object {

        val ARTICLE_DB = "article.db"

        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        fun getInstance(context: Context): ArticleDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, ArticleDatabase::class.java, ARTICLE_DB)
                .build()
    }
}
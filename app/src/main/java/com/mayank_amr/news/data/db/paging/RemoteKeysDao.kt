package com.mayank_amr.news.data.db.paging

import androidx.room.*

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 08:47 PM
 */

/**
 * This class is Data Access object for paging
 */

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remotekeys WHERE repoId = :id AND category = :category")
    suspend fun remoteKeysArticleId(id: String, category: String): RemoteKeys?

    @Query("DELETE FROM remotekeys WHERE category = :category")
    suspend fun clearRemoteKeys(category: String)

    @Update
    suspend fun updateRemoteKeys(remoteKey: RemoteKeys)


}
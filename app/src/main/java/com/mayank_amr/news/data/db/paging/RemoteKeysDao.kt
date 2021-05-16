package com.mayank_amr.news.data.db.paging

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 08:47 PM
 */
@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remotekeys WHERE repoId = :id")
    suspend fun remoteKeysArticleId(id: String): RemoteKeys?

    @Query("DELETE FROM remotekeys")
    suspend fun clearRemoteKeys()
}
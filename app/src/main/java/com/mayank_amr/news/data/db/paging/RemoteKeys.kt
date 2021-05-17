package com.mayank_amr.news.data.db.paging

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Project News
 * @Created_by Mayank Kumar on 16-05-2021 08:46 PM
 */

/**
* This data class contain Keys for paging library and
 * category to filter article at offline
*/

@Entity
data class RemoteKeys(
    @PrimaryKey val repoId: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val category: String
)
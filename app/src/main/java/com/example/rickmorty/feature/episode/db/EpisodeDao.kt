package com.example.rickmorty.feature.episode.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<EpisodeDto>)

    @Query("SELECT * FROM episodes")
    fun getAllEpisodes(): PagingSource<Int, EpisodeDto>

    @Query("DELETE FROM episodes")
    suspend fun clearCharacters()

    @Query("SELECT * FROM episodes WHERE id IN (:idList)")
    fun getEpisodeByIds(idList: List<Int>): PagingSource<Int, EpisodeDto>

    @Query(
        "SELECT * FROM episodes WHERE (:name IS NULL OR name LIKE '%' || :name || '%') " +
                "AND (:episode IS NULL OR episode LIKE '%' || :episode || '%') "
    )
    fun getFilteredEpisodes(
        name: String?,
        episode: String?
    ): PagingSource<Int, EpisodeDto>
}
package com.example.rickmorty.feature.episode.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EpisodeRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<EpisodeRemoteKeys>)

    @Query("SELECT * FROM episode_keys WHERE id = :id")
    suspend fun remoteKeysEpisodeId(id: Int): EpisodeRemoteKeys?

    @Query("DELETE FROM episode_keys")
    suspend fun clearRemoteKeys()
}
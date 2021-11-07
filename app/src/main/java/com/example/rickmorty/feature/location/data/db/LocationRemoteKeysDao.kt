package com.example.rickmorty.feature.location.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocationRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<LocationRemoteKeys>)

    @Query("SELECT * FROM location_keys WHERE id = :id")
    suspend fun remoteKeysLocationId(id: Int): LocationRemoteKeys?

    @Query("DELETE FROM location_keys")
    suspend fun clearRemoteKeys()
}
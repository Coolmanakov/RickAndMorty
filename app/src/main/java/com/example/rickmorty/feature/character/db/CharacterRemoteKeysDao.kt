package com.example.rickmorty.feature.character.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CharacterRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<CharacterRemoteKeys>)

    @Query("SELECT * FROM character_keys WHERE id = :id")
    suspend fun remoteKeysByCharacterId(id: Int): CharacterRemoteKeys?

    @Query("DELETE FROM character_keys")
    suspend fun clearRemoteKeys()
}
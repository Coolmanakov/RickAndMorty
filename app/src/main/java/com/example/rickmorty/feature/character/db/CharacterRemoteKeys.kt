package com.example.rickmorty.feature.character.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_keys")
data class CharacterRemoteKeys(
    @PrimaryKey
    val id : Int,
    val prevKey: Int?,
    val nextKey: Int?
)
package com.example.rickmorty.feature.episode.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "episode_keys")
data class EpisodeRemoteKeys(
    @PrimaryKey
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)

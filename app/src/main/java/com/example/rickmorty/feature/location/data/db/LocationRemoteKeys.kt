package com.example.rickmorty.feature.location.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_keys")
data class LocationRemoteKeys(
    @PrimaryKey
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?
)

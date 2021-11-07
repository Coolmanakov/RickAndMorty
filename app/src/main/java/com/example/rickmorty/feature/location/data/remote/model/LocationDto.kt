package com.example.rickmorty.feature.location.data.remote.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializer

@Parcelize
@Entity(tableName = "locations")
data class LocationDto(
    @PrimaryKey
    val id: Int,
    val name: String,
    val dimension: String,
    val type: String,
    val created: String,
    @field:SerializedName("residents") val characters: List<String>,
    val url: String
) : Parcelable
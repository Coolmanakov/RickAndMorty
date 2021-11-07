package com.example.rickmorty.feature.character.data.remote.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
@Entity(tableName = "characters")
data class CharacterDto(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: @RawValue OriginDto,
    @field:SerializedName("location") val characterLocation: @RawValue OriginDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
) : Parcelable
package com.example.rickmorty.feature.character.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Parcelize
data class OriginDto(
    val name: String,
    val url: String
) : Parcelable
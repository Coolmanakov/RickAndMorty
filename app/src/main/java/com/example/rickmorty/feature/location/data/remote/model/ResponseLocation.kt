package com.example.rickmorty.feature.location.data.remote.model

import com.example.rickmorty.util.model.InfoResponseDto
import com.google.gson.annotations.SerializedName

data class ResponseLocation(
    val info: InfoResponseDto,
    @field:SerializedName("results") val locations: List<LocationDto>
)

package com.example.rickmorty.feature.episode.data.remote.model

import com.example.rickmorty.util.model.InfoResponseDto
import com.google.gson.annotations.SerializedName

data class ResponseEpisode(
    val info: InfoResponseDto,
    @field:SerializedName("results") val episodes: List<EpisodeDto>
)

package com.example.rickmorty.feature.episode.data.remote.api

import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.data.remote.model.ResponseEpisode
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EpisodeService {

    @GET("episode")
    suspend fun getAllEpisodes(
        @Query("page") page: Int
    ): ResponseEpisode

    @GET("episode/{id}")
    suspend fun getMultipleEpisodes(
        @Path(value = "id") id: List<Int>
    ): List<EpisodeDto>

    @GET("episode")
    suspend fun getFilteredEpisodeList(
        @Query("name") name: String?,
        @Query("episode") episode: String?,
        @Query("page") page: Int
    ) : ResponseEpisode
}
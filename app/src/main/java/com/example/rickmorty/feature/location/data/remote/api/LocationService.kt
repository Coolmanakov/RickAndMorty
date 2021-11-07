package com.example.rickmorty.feature.location.data.remote.api

import com.example.rickmorty.feature.location.data.remote.model.LocationDto
import com.example.rickmorty.feature.location.data.remote.model.ResponseLocation
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationService {

    @GET("location")
    suspend fun getAllLocations(
        @Query("page") page: Int
    ): ResponseLocation

    @GET("location/{id}")
    suspend fun getSingleLocation(
        @Path(value = "id") id: Int
    ): LocationDto?

    @GET("location")
    suspend fun getFilteredLocationList(
        @Query("name") name: String?,
        @Query("type") type: String?,
        @Query("dimension") dimension: String?,
        @Query("page") page: Int
    ) : ResponseLocation
}
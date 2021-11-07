package com.example.rickmorty.feature.character.data.remote.api

import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.remote.model.ResponseCharacter
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharacterService {

    @GET("character")
    suspend fun getAllCharacters(
        @Query("page") page: Int
    ): ResponseCharacter

    @GET("character/{id}")
    suspend fun getMultipleCharacters(
        @Path(value = "id") id: List<Int>
    ): List<CharacterDto>

    @GET("character")
    suspend fun getFilteredCharacterList(
        @Query("name") name: String?,
        @Query("status") status: String?,
        @Query("species") species: String?,
        @Query("type") type: String?,
        @Query("gender") gender: String?,
        @Query("page") page: Int
    ) : ResponseCharacter
}
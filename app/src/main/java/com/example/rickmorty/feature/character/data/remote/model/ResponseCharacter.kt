package com.example.rickmorty.feature.character.data.remote.model

import com.example.rickmorty.util.model.InfoResponseDto
import com.google.gson.annotations.SerializedName


data class ResponseCharacter(
    val info: InfoResponseDto,
    @field:SerializedName("results")val characters: List<CharacterDto>
)

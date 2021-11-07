package com.example.rickmorty.feature.details_location.presentation

import androidx.paging.PagingData
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.repository.CharacterRepository
import com.example.rickmorty.feature.details_location.repository.LocationDetailsRepository
import com.example.rickmorty.feature.location.data.remote.model.LocationDto
import com.example.rickmorty.feature.location.data.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationDetailsViewModel @Inject constructor(
    private val repositoryCharacter: CharacterRepository,
    private val repositoryLocation: LocationDetailsRepository
) {

    suspend fun searchSingleLocation(url: String): Pair<LocationDto?, Flow<PagingData<CharacterDto>>>? {
        val location = repositoryLocation.searchSingleLocation(url.getId())
        if (location != null) {
            return Pair(location, location.characters.getCharactersIds()
                .let { repositoryCharacter.searchCharactersByIds(it) })
        }
        return null
    }

    private fun List<String>.getCharactersIds(): List<Int> =
        this.map {
            it.substringAfterLast("/").toInt()
        }

    private fun String.getId(): Int =
        this.substringAfterLast("/").toInt()

}
package com.example.rickmorty.feature.details_character.presentation

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.rickmorty.feature.details_character.repository.CharacterDetailsRepository
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterDetailsViewModel @Inject constructor(
    private val repository: CharacterDetailsRepository
) : ViewModel() {

    fun searchEpisodesByIds(idList: List<Int>): Flow<PagingData<EpisodeDto>> {
        return repository.searchEpisodesByIds(idList)
    }
}
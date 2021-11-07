package com.example.rickmorty.feature.details_episode.presentation

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EpisodeDetailsViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {

    fun searchCharactersByIds(idList: List<Int>): Flow<PagingData<CharacterDto>> =
        repository.searchCharactersByIds(idList)
}
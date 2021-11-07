package com.example.rickmorty.feature.character.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickmorty.feature.character.data.repository.CharacterRepository


import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.remote.model.FilterCharacter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class CharactersViewModel @Inject constructor(
    private val repository: CharacterRepository
) : ViewModel() {
    val characters: Flow<PagingData<CharacterDto>>

    init {
        characters = searchAllCharacters()
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
    }

    private fun searchAllCharacters(): Flow<PagingData<CharacterDto>> =
        repository.searchCharacters()

    fun searchFilteredList(character: FilterCharacter): Flow<PagingData<CharacterDto>> =
        repository
            .filterCharacters(character)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
}


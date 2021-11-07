package com.example.rickmorty.feature.episode.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.data.remote.model.FilterEpisode
import com.example.rickmorty.feature.episode.data.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class EpisodeViewModel @Inject constructor(
    private val repository: EpisodeRepository
) : ViewModel() {

    val episodes: Flow<PagingData<EpisodeDto>>

    init {
        episodes = searchEpisodes()
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
    }

    private fun searchEpisodes(): Flow<PagingData<EpisodeDto>> {
        return repository.searchEpisodes()
    }

    fun searchFilteredEpisodes(filterEpisode: FilterEpisode): Flow<PagingData<EpisodeDto>>{
        return repository.searchFilteredEpisodes(filterEpisode)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
    }
}
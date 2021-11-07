package com.example.rickmorty.feature.location.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickmorty.feature.location.data.remote.model.FilterLocation
import com.example.rickmorty.feature.location.data.remote.model.LocationDto
import com.example.rickmorty.feature.location.data.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class LocationViewModel@Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    val locations: Flow<PagingData<LocationDto>>

    init {
        locations = searchEpisodes()
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
    }

    private fun searchEpisodes(): Flow<PagingData<LocationDto>> {
        return repository.searchAllLocations()
    }

    fun filterLocations(filterLocation: FilterLocation): Flow<PagingData<LocationDto>>{
        return repository.filterLocation(filterLocation)
    }
}

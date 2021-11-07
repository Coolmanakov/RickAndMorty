package com.example.rickmorty.feature.location.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.character.data.repository.CharacterRepository
import com.example.rickmorty.feature.location.data.remote.api.LocationService
import com.example.rickmorty.feature.location.data.remote.model.FilterLocation
import com.example.rickmorty.feature.location.data.remote.model.LocationDto
import com.example.rickmorty.feature.location.data.remote.source.LocationRemoteMediator
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val service: LocationService,
    private val database: RickMortyDatabase
) {

    fun searchAllLocations(): Flow<PagingData<LocationDto>> {
        val pagingSourceFactory = {
            database.locationDao().getAllLocations()
        }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = LocationRemoteMediator(
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun filterLocation(filterLocation: FilterLocation): Flow<PagingData<LocationDto>> {
        val pagingSourceFactory = {
            database.locationDao().getFilteredCharacters(
                name = if (filterLocation.name == "") null else filterLocation.name,
                type = if (filterLocation.type == "") null else filterLocation.type,
                dimension = if (filterLocation.dimension == "") null else filterLocation.dimension,
            )
        }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = CharacterRepository.NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = LocationRemoteMediator(
                service,
                database,
                filterLocation
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow

    }

    companion object {
        const val NETWORK_PAGE_SIZE = 31
    }
}
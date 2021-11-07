package com.example.rickmorty.feature.location.data.remote.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.location.data.db.LocationRemoteKeys
import com.example.rickmorty.feature.location.data.remote.api.LocationService
import com.example.rickmorty.feature.location.data.remote.model.FilterLocation
import com.example.rickmorty.feature.location.data.remote.model.LocationDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class LocationRemoteMediator @Inject constructor(
    private val service: LocationService,
    private val database: RickMortyDatabase,
    private val filterLocation: FilterLocation? = null
) : RemoteMediator<Int, LocationDto>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationDto>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            if (filterLocation == null) {
                val endOfPaginationReached = loadAllLocations(loadType, page)
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                val endOfPaginationReached = loadFilteredLocations(filterLocation, page)
                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun loadAllLocations(
        loadType: LoadType,
        page: Int
    ): Boolean {

        val apiResponse = service.getAllLocations(page)

        val locations = apiResponse.locations
        val endOfPaginationReached = apiResponse.info.next == null
        database.withTransaction {
            // clear all tables in the database
            if (loadType == LoadType.REFRESH) {
                database.locationKeysDao().clearRemoteKeys()
                database.locationDao().clearLocation()
            }
            val prevKey =
                if (page == STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = locations.map {
                LocationRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            database.locationKeysDao().insertAll(keys)
            database.locationDao().insertAll(locations)
        }
        return endOfPaginationReached
    }

    private suspend fun loadFilteredLocations(
        filterLocation: FilterLocation,
        page: Int
    ): Boolean {
        val apiResponse = service.getFilteredLocationList(
            name = filterLocation.name,
            type = filterLocation.type,
            dimension = filterLocation.dimension,
            page = page
        )
        val locations = apiResponse.locations
        val endOfPaginationReached = apiResponse.info.next == null
        database.withTransaction {
            database.locationDao().insertAll(locations)
        }
        return endOfPaginationReached
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, LocationDto>
    ): LocationRemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { location ->
                // Get the remote keys of the last item retrieved
                database.locationKeysDao().remoteKeysLocationId(location.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, LocationDto>
    ): LocationRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { location ->
                database.locationKeysDao().remoteKeysLocationId(location.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, LocationDto>
    ): LocationRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.locationKeysDao().remoteKeysLocationId(id)
            }
        }
    }
}
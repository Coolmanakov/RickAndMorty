package com.example.rickmorty.feature.episode.data.remote.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickmorty.util.UserAction
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.episode.data.remote.api.EpisodeService
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.data.remote.model.FilterEpisode
import com.example.rickmorty.feature.episode.db.EpisodeRemoteKeys
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class EpisodeRemoteMediator @Inject constructor(
    private val service: EpisodeService,
    private val database: RickMortyDatabase,
    private val userAction: UserAction<out Any>
) : RemoteMediator<Int, EpisodeDto>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeDto>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1)
                    ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        try {
            val endOfPaginationReached = when(userAction){
                is UserAction.FindAllItemAction -> loadAllEpisodes(
                    page = page,
                    loadType = loadType
                )
                is UserAction.FindItemByIdsAction -> loadMultipleEpisodes(userAction.idList)
                is UserAction.FilterAction -> loadFilteredEpisodes(
                    userAction.filter as FilterEpisode,
                    page
                )
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun loadAllEpisodes(
        loadType: LoadType,
        page: Int
    ): Boolean {
        val apiResponse = service.getAllEpisodes(page)

        val episodes = apiResponse.episodes
        val endOfPaginationReached = apiResponse.info.next == null
        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                database.episodeKeysDao().clearRemoteKeys()
                database.episodeDao().clearCharacters()
            }
            val prevKey =
                if (page == STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = episodes.map {
                EpisodeRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            database.episodeKeysDao().insertAll(keys)
            database.episodeDao().insertAll(episodes)
        }
        return endOfPaginationReached
    }

    private suspend fun loadMultipleEpisodes(
        idList: List<Int>
    ): Boolean {
        val episodes = service.getMultipleEpisodes(idList)
        database.episodeDao().insertAll(episodes)
        return true
    }

    private suspend fun loadFilteredEpisodes(
        filterEpisode: FilterEpisode,
        page: Int
    ): Boolean {
        val apiResponse = service.getFilteredEpisodeList(
            name = filterEpisode.name,
            episode = filterEpisode.episode,
            page = page
        )

        val episodes = apiResponse.episodes
        val endOfPaginationReached = apiResponse.info.next == null
        database.withTransaction {
            database.episodeDao().insertAll(episodes)
        }
        return endOfPaginationReached
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, EpisodeDto>
    ): EpisodeRemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { episode ->
                // Get the remote keys of the last item retrieved
                database.episodeKeysDao().remoteKeysEpisodeId(episode.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, EpisodeDto>
    ): EpisodeRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { character ->
                database.episodeKeysDao().remoteKeysEpisodeId(character.id)
            }

    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, EpisodeDto>
    ): EpisodeRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.episodeKeysDao().remoteKeysEpisodeId(id)
            }
        }
    }
}
package com.example.rickmorty.feature.episode.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.rickmorty.util.UserAction
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.episode.data.remote.api.EpisodeService
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.data.remote.model.FilterEpisode
import com.example.rickmorty.feature.episode.data.remote.source.EpisodeRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class EpisodeRepository @Inject constructor(
    private val service: EpisodeService,
    private val database: RickMortyDatabase
) {
    fun searchEpisodes(): Flow<PagingData<EpisodeDto>> {
        val userAction = UserAction.FindAllItemAction()
        val pagingSourceFactory = {
            database.episodeDao().getAllEpisodes()
        }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = EpisodeRepository.NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = EpisodeRemoteMediator(
                service,
                database,
                userAction
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchFilteredEpisodes(filterEpisode: FilterEpisode): Flow<PagingData<EpisodeDto>> {
        val userAction = UserAction.FilterAction(filterEpisode)
        val pagingSourceFactory = {
            database.episodeDao().getFilteredEpisodes(
                name = if(filterEpisode.name == "") null else filterEpisode.name,
                episode = if(filterEpisode.episode == "") null else filterEpisode.episode
            )
        }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = EpisodeRepository.NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = EpisodeRemoteMediator(
                service,
                database,
                userAction
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}

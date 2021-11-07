package com.example.rickmorty.feature.details_character.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.rickmorty.util.UserAction
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.episode.data.remote.api.EpisodeService
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.data.remote.source.EpisodeRemoteMediator
import com.example.rickmorty.feature.episode.data.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterDetailsRepository @Inject constructor(
    private val database: RickMortyDatabase,
    private val service: EpisodeService
) {
    fun searchEpisodesByIds(idList: List<Int>): Flow<PagingData<EpisodeDto>> {
        val userAction = UserAction.FindItemByIdsAction(idList)
        val pagingSourceFactory = {
            database.episodeDao().getEpisodeByIds(idList)
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
}
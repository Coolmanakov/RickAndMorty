package com.example.rickmorty.feature.character.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.rickmorty.util.UserAction
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.character.data.remote.api.CharacterService
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.remote.model.FilterCharacter
import com.example.rickmorty.feature.character.data.remote.source.CharacterRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterRepository @Inject constructor(
    private val service: CharacterService,
    private val database: RickMortyDatabase
) {
    fun searchCharacters(): Flow<PagingData<CharacterDto>> {
        val userAction = UserAction.FindAllItemAction()
        val pagingSourceFactory = {
            database.charactersDao().getAllCharacters()
        }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = CharacterRemoteMediator(
                service,
                database,
                userAction
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun filterCharacters(character: FilterCharacter): Flow<PagingData<CharacterDto>> {
        val userAction = UserAction.FilterAction(character)
        val pagingSourceFactory = {
            database.charactersDao().getFilteredCharacters(
                name = if (character.name == "") null else character.name,
                status = if (character.status == "") null else character.status,
                species = if (character.species == "") null else character.species,
                type = if (character.type == "") null else character.type,
                gender = if (character.gender == "") null else character.gender,
            )
        }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(
                service,
                database,
                userAction
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchCharactersByIds(idList: List<Int>): Flow<PagingData<CharacterDto>> {
        val userAction = UserAction.FindItemByIdsAction(idList)
        val pagingSourceFactory = {
            database.charactersDao().getCharactersByIds(idList)
        }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = CharacterRepository.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(
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
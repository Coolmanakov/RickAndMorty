package com.example.rickmorty.feature.character.data.remote.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickmorty.util.UserAction
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.character.data.remote.api.CharacterService
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.remote.model.FilterCharacter
import com.example.rickmorty.feature.character.db.CharacterRemoteKeys
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator @Inject constructor(
    private val service: CharacterService,
    private val database: RickMortyDatabase,
    private val userAction: UserAction<out Any>
) : RemoteMediator<Int, CharacterDto>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterDto>
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
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val endOfPaginationReached = when (userAction) {
                is UserAction.FindAllItemAction -> loadAllCharacters(
                    page = page,
                    loadType = loadType
                )
                is UserAction.FindItemByIdsAction -> loadMultipleCharacters(userAction.idList)
                is UserAction.FilterAction -> getFilteredList(
                    userAction.filter as FilterCharacter,
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

    private suspend fun loadAllCharacters(
        page: Int,
        loadType: LoadType
    ): Boolean {
        val apiResponse = service.getAllCharacters(page)

        val characters = apiResponse.characters
        val endOfPaginationReached = apiResponse.info.next == null
        database.withTransaction {
            // clear all tables in the database
            if (loadType == LoadType.REFRESH) {
                database.characterKeysDao().clearRemoteKeys()
                database.charactersDao().clearCharacters()
            }
            val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = characters.map {
                CharacterRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            database.characterKeysDao().insertAll(keys)
            database.charactersDao().insertAll(characters)
        }
        return endOfPaginationReached
    }

    private suspend fun loadMultipleCharacters(
        idList: List<Int>
    ): Boolean {
        val characters = service.getMultipleCharacters(idList)
        database.charactersDao().insertAll(characters)
        return true
    }

    private suspend fun getFilteredList(
        character: FilterCharacter,
        page: Int
    ): Boolean {
        val apiResponse = service.getFilteredCharacterList(
            character.name,
            character.status,
            character.species,
            character.type,
            character.gender,
            page
        )

        val characters = apiResponse.characters
        val endOfPaginationReached = apiResponse.info.next == null
        database.withTransaction {
            database.charactersDao().insertAll(characters)
        }
        return endOfPaginationReached
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, CharacterDto>
    ): CharacterRemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character ->
                // Get the remote keys of the last item retrieved
                database.characterKeysDao().remoteKeysByCharacterId(character.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, CharacterDto>
    ): CharacterRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { character ->
                database.characterKeysDao().remoteKeysByCharacterId(character.id)
            }

    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, CharacterDto>
    ): CharacterRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.characterKeysDao().remoteKeysByCharacterId(id)
            }
        }
    }
}

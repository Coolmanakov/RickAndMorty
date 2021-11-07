package com.example.rickmorty.feature.character.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterDto>)

    @Query("SELECT * FROM characters")
    fun getAllCharacters(): PagingSource<Int, CharacterDto>

    @Query("SELECT * FROM characters WHERE id IN (:idList)")
    fun getCharactersByIds(idList: List<Int>): PagingSource<Int, CharacterDto>

    @Query(
        "SELECT * FROM characters WHERE (:name IS NULL OR name  LIKE '%' || :name || '%') " +
                "AND (:status IS NULL OR status LIKE :status) " +
                "AND (:species IS NULL OR species LIKE '%' || :species || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type || '%') " +
                "AND (:gender IS NULL OR gender LIKE :gender) "
    )
    fun getFilteredCharacters(
        name: String?,
        status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): PagingSource<Int, CharacterDto>

    @Query("DELETE FROM characters")
    suspend fun clearCharacters()
}
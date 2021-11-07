package com.example.rickmorty.feature.location.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickmorty.feature.location.data.remote.model.LocationDto

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(location: List<LocationDto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingle(location: LocationDto)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): PagingSource<Int, LocationDto>

    @Query("DELETE FROM locations")
    suspend fun clearLocation()

    @Query("SELECT * FROM locations WHERE id =:id")
    suspend fun getSingleLocation(id: Int): LocationDto?

    @Query(
        "SELECT * FROM locations WHERE (:name IS NULL OR name LIKE '%' || :name || '%') " +
                "AND (:type IS NULL OR type LIKE '%' || :type || '%') " +
                "AND (:dimension IS NULL OR dimension LIKE '%' || :dimension || '%')"
    )
    fun getFilteredCharacters(
        name: String?,
        type: String?,
        dimension: String?
    ): PagingSource<Int, LocationDto>
}
package com.example.rickmorty.feature.details_location.repository

import android.util.Log
import com.example.rickmorty.db.RickMortyDatabase
import com.example.rickmorty.feature.location.data.remote.api.LocationService
import com.example.rickmorty.feature.location.data.remote.model.LocationDto
import java.io.IOException
import javax.inject.Inject

class LocationDetailsRepository @Inject constructor(
    private val database: RickMortyDatabase,
    private val locationService: LocationService
) {

    suspend fun searchSingleLocation(id: Int): LocationDto? {
        var location = database.locationDao().getSingleLocation(id)
        if(location == null){
            try {
                location = locationService.getSingleLocation(id)
            }
            catch (e: IOException){
                Log.e("mLog", e.message!!)
            }
            if (location != null) {
                database.locationDao().insertSingle(location)
            }
        }
        return location
    }
}
package com.example.rickmorty.di.modules

import com.example.rickmorty.feature.character.data.remote.api.CharacterService
import com.example.rickmorty.feature.episode.data.remote.api.EpisodeService
import com.example.rickmorty.feature.location.data.remote.api.LocationService
import com.example.rickmorty.retrofit.RetrofitUtils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideCharacterService(): CharacterService =
        RetrofitUtils.createService(CharacterService::class.java)

    @Singleton
    @Provides
    fun provideEpisodeService(): EpisodeService =
        RetrofitUtils.createService(EpisodeService::class.java)

    @Singleton
    @Provides
    fun provideLocationService(): LocationService =
        RetrofitUtils.createService(LocationService::class.java)

}
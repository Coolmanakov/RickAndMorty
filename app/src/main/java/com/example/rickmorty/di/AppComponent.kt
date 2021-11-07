package com.example.rickmorty.di

import android.content.Context
import com.example.rickmorty.di.modules.AppModule
import com.example.rickmorty.di.modules.DatabaseModule
import com.example.rickmorty.di.modules.NetworkModule
import com.example.rickmorty.feature.character.presentation.CharacterFragment
import com.example.rickmorty.feature.details_character.presentation.CharacterDetailsFragment
import com.example.rickmorty.feature.details_episode.presentation.EpisodeDetailsFragment
import com.example.rickmorty.feature.details_location.presentation.LocationDetailsFragment
import com.example.rickmorty.feature.episode.presentation.EpisodeFragment
import com.example.rickmorty.feature.location.presentation.LocationFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, NetworkModule::class, AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: CharacterFragment)
    fun inject(fragment: CharacterDetailsFragment)
    fun inject(fragment: EpisodeFragment)
    fun inject(fragment: LocationFragment)
    fun inject(fragment: EpisodeDetailsFragment)
    fun inject(fragment: LocationDetailsFragment)
}
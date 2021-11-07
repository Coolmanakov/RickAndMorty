package com.example.rickmorty.di.modules

import android.content.Context
import com.example.rickmorty.db.RickMortyDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): RickMortyDatabase {
        return RickMortyDatabase.getInstance(context)
    }
}
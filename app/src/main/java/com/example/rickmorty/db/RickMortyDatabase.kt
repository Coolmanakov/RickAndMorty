package com.example.rickmorty.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rickmorty.feature.character.data.remote.model.CharacterDto
import com.example.rickmorty.feature.character.data.remote.model.converters.EpisodeConverter
import com.example.rickmorty.feature.character.data.remote.model.converters.OriginConverter
import com.example.rickmorty.feature.character.db.CharacterDao
import com.example.rickmorty.feature.character.db.CharacterRemoteKeys
import com.example.rickmorty.feature.character.db.CharacterRemoteKeysDao
import com.example.rickmorty.feature.episode.data.remote.model.EpisodeDto
import com.example.rickmorty.feature.episode.db.EpisodeDao
import com.example.rickmorty.feature.episode.db.EpisodeRemoteKeys
import com.example.rickmorty.feature.episode.db.EpisodeRemoteKeysDao
import com.example.rickmorty.feature.location.data.db.LocationDao
import com.example.rickmorty.feature.location.data.db.LocationRemoteKeys
import com.example.rickmorty.feature.location.data.db.LocationRemoteKeysDao
import com.example.rickmorty.feature.location.data.remote.model.LocationDto

@Database(
    entities = [
        CharacterDto::class,
        CharacterRemoteKeys::class,
        EpisodeDto::class,
        EpisodeRemoteKeys::class,
        LocationDto::class,
        LocationRemoteKeys::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(OriginConverter::class, EpisodeConverter::class)
abstract class RickMortyDatabase : RoomDatabase() {

    abstract fun charactersDao(): CharacterDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun characterKeysDao(): CharacterRemoteKeysDao
    abstract fun episodeKeysDao(): EpisodeRemoteKeysDao
    abstract fun locationDao(): LocationDao
    abstract fun locationKeysDao(): LocationRemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: RickMortyDatabase? = null

        fun getInstance(context: Context): RickMortyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RickMortyDatabase::class.java, "RickMorty.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}
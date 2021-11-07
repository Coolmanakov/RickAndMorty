package com.example.rickmorty.feature.character.data.remote.model.converters

import androidx.room.TypeConverter

class EpisodeConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromEpisode(value: List<String>): String =
            value.joinToString(separator = "&")


        @TypeConverter
        @JvmStatic
        fun toEpisode(value: String): List<String> =
            value.split("&")
    }
}
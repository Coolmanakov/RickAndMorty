package com.example.rickmorty.feature.character.data.remote.model.converters

import androidx.room.TypeConverter
import com.example.rickmorty.feature.character.data.remote.model.OriginDto

class OriginConverter {

    companion object {
        @TypeConverter
        @JvmStatic
        fun fromOrigin(value: OriginDto): String =
            value.name.plus("/${value.url}")

        @TypeConverter
        @JvmStatic
        fun toOrigin(value: String): OriginDto =
            OriginDto(
                name = value.substringBefore("/"),
                url = value.substringAfter("/")
            )
    }
}

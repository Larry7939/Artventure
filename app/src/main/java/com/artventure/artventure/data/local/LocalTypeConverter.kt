package com.artventure.artventure.data.local

import androidx.room.TypeConverter
import com.artventure.artventure.data.model.dto.CollectionDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalTypeConverter {
    companion object {
        private val gson = Gson()

        @TypeConverter
        @JvmStatic
        fun fromCollectionDto(collectionDto: CollectionDto): String {
            return gson.toJson(collectionDto)
        }

        @TypeConverter
        @JvmStatic
        fun toCollectionDto(json: String): CollectionDto {
            val type = object : TypeToken<CollectionDto>() {}.type
            return gson.fromJson(json, type)
        }
    }
}
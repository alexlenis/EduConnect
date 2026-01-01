package com.example.educonnect.data.database

import androidx.room.TypeConverter
import com.example.educonnect.data.entity.PlaceType

class Converters {

    @TypeConverter
    fun fromPlaceType(type: PlaceType): String = type.name

    @TypeConverter
    fun toPlaceType(value: String): PlaceType = PlaceType.valueOf(value)
}

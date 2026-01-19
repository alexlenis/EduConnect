package com.example.educonnect.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_places")
data class MapPlace(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,

    val type: PlaceType,


    val subjectId: Int? = null
)

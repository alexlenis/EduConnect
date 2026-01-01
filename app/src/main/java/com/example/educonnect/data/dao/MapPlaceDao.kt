package com.example.educonnect.data.dao

import androidx.room.*
import com.example.educonnect.data.entity.MapPlace

@Dao
interface MapPlaceDao {

    @Query("SELECT * FROM map_places")
    suspend fun getAll(): List<MapPlace>

    @Query("SELECT * FROM map_places WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): MapPlace?

    @Insert
    suspend fun insert(place: MapPlace): Long

    @Update
    suspend fun update(place: MapPlace)

    @Delete
    suspend fun delete(place: MapPlace)
}

package com.example.filmcollection.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.filmcollection.model.Film
import com.example.filmcollection.model.WatchStatus

@Entity(tableName = "films")
data class FilmEntity(
    @PrimaryKey val id: String,
    val title: String,
    val year: Int,
    val genre: String,
    val country: String,
    val director: String,
    val rating: Int?,
    val watchStatus: WatchStatus,
    val imageResId: Int?,
    val isRemote: Boolean,
)

fun FilmEntity.toDomain(): Film = Film(
    id = id,
    title = title,
    year = year,
    genre = genre,
    country = country,
    director = director,
    rating = rating,
    watchStatus = watchStatus,
    imageResId = imageResId,
    isRemote = isRemote,
)

fun Film.toEntity(): FilmEntity = FilmEntity(
    id = id,
    title = title,
    year = year,
    genre = genre,
    country = country,
    director = director,
    rating = rating,
    watchStatus = watchStatus,
    imageResId = imageResId,
    isRemote = isRemote,
)

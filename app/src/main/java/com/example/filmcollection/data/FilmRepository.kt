package com.example.filmcollection.data

import com.example.filmcollection.model.Film
import kotlinx.coroutines.flow.Flow

interface FilmRepository {
    val films: Flow<List<Film>>

    suspend fun addFilm(
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ): Film

    suspend fun updateFilm(film: Film)

    suspend fun deleteFilm(id: String)

    suspend fun refresh(): Result<Unit>
}

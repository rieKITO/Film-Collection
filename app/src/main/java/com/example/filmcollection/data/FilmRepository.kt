package com.example.filmcollection.data

import com.example.filmcollection.model.Film
import kotlinx.coroutines.flow.StateFlow

interface FilmRepository {
    val films: StateFlow<List<Film>>

    fun addFilm(
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ): Film

    fun updateFilm(film: Film)

    fun deleteFilm(id: String)

    suspend fun refresh(): Result<Unit>
}

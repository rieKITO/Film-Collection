package com.example.filmcollection.data.remote

import com.example.filmcollection.model.Film

interface FilmRemoteDataSource {
    suspend fun fetchFilms(): List<Film>
}

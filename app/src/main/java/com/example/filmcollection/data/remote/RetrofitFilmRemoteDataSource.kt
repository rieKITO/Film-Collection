package com.example.filmcollection.data.remote

import com.example.filmcollection.model.Film
import com.example.filmcollection.network.FilmApi
import com.example.filmcollection.network.dto.MovieDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitFilmRemoteDataSource(
    private val api: FilmApi,
) : FilmRemoteDataSource {

    override suspend fun fetchFilms(): List<Film> = withContext(Dispatchers.IO) {
        val response = api.getMovies()
        response.movies
            .orEmpty()
            .mapNotNull { it.toFilmOrNull() }
    }

    private fun MovieDto.toFilmOrNull(): Film? {
        val id = id?.takeIf { it.isNotBlank() } ?: return null
        val title = title?.takeIf { it.isNotBlank() } ?: return null
        val year = year ?: return null
        return Film(
            id = id,
            title = title,
            year = year,
            genre = genre.orEmpty(),
            country = country.orEmpty(),
            director = director.orEmpty(),
            isRemote = true,
        )
    }
}

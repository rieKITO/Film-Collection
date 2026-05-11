package com.example.filmcollection.data

import com.example.filmcollection.data.remote.FilmRemoteDataSource
import com.example.filmcollection.model.Film
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InMemoryFilmRepository(
    initialFilms: List<Film> = emptyList(),
    private val remoteDataSource: FilmRemoteDataSource? = null,
) : FilmRepository {
    private val _films = MutableStateFlow(initialFilms)
    override val films: StateFlow<List<Film>> = _films

    override fun addFilm(
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ): Film {
        val film = Film(
            id = UUID.randomUUID().toString(),
            title = title,
            year = year,
            genre = genre,
            country = country,
            director = director,
        )
        _films.update { it + film }
        return film
    }

    override fun updateFilm(film: Film) {
        _films.update { current ->
            current.map { if (it.id == film.id) film else it }
        }
    }

    override fun deleteFilm(id: String) {
        _films.update { current -> current.filterNot { it.id == id } }
    }

    override suspend fun refresh(): Result<Unit> {
        val source = remoteDataSource ?: return Result.success(Unit)
        return runCatching {
            val remoteFilms = source.fetchFilms()
            _films.update { current -> merge(current, remoteFilms) }
        }
    }

    private fun merge(local: List<Film>, remote: List<Film>): List<Film> {
        val localById = local.associateBy { it.id }
        val remoteById = remote.associateBy { it.id }

        val merged = remote.map { incoming ->
            val existing = localById[incoming.id]
            if (existing != null) {
                incoming.copy(
                    rating = existing.rating,
                    watchStatus = existing.watchStatus,
                    imageResId = existing.imageResId,
                )
            } else {
                incoming
            }
        }

        val userAdded = local.filter { !it.isRemote && it.id !in remoteById }

        return merged + userAdded
    }
}

package com.example.filmcollection.data

import com.example.filmcollection.data.remote.FilmRemoteDataSource
import com.example.filmcollection.model.Film
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Тестовый fake-репозиторий. В проде используется [RoomFilmRepository].
 */
class InMemoryFilmRepository(
    initialFilms: List<Film> = emptyList(),
    private val remoteDataSource: FilmRemoteDataSource? = null,
) : FilmRepository {
    private val _films = MutableStateFlow(initialFilms)
    override val films: Flow<List<Film>> = _films

    fun currentFilms(): List<Film> = _films.value

    override suspend fun addFilm(
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

    override suspend fun updateFilm(film: Film) {
        _films.update { current ->
            current.map { if (it.id == film.id) film else it }
        }
    }

    override suspend fun deleteFilm(id: String) {
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

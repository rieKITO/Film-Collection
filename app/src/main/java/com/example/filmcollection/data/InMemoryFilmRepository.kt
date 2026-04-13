package com.example.filmcollection.data

import com.example.filmcollection.model.Film
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InMemoryFilmRepository(
    initialFilms: List<Film> = emptyList(),
) : FilmRepository {
    private val _films = MutableStateFlow(initialFilms)
    override val films: StateFlow<List<Film>> = _films

    private val nextId = AtomicLong((initialFilms.maxOfOrNull { it.id } ?: 0L) + 1L)

    override fun addFilm(
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ): Film {
        val film = Film(
            id = nextId.getAndIncrement(),
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

    override fun deleteFilm(id: Long) {
        _films.update { current -> current.filterNot { it.id == id } }
    }
}

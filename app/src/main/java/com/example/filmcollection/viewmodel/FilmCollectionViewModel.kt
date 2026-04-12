package com.example.filmcollection.viewmodel

import androidx.lifecycle.ViewModel
import com.example.filmcollection.data.FilmRepository
import com.example.filmcollection.model.Film
import kotlinx.coroutines.flow.StateFlow

class FilmCollectionViewModel(
    private val repository: FilmRepository,
) : ViewModel() {
    val films: StateFlow<List<Film>> = repository.films

    fun addFilm(
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ) {
        repository.addFilm(
            title = title,
            year = year,
            genre = genre,
            country = country,
            director = director,
        )
    }

    fun updateFilm(film: Film) {
        repository.updateFilm(film)
    }

    fun deleteFilm(id: Long) {
        repository.deleteFilm(id)
    }
}

package com.example.filmcollection.viewmodel

import androidx.lifecycle.ViewModel
import com.example.filmcollection.data.FilmRepository
import com.example.filmcollection.model.Film
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class FilmCollectionViewModel(
    private val repository: FilmRepository,
) : ViewModel() {
    val films: StateFlow<List<Film>> = repository.films

    private val _snackbarChannel = Channel<String>(Channel.BUFFERED)
    val snackbarEvents: Flow<String> = _snackbarChannel.receiveAsFlow()

    fun addFilm(
        title: String,
        year: Int,
        genre: String,
        country: String,
        director: String,
    ): Film {
        return repository.addFilm(
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

    fun showSnackbar(message: String) {
        _snackbarChannel.trySend(message)
    }
}

package com.example.filmcollection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmcollection.data.FilmRepository
import com.example.filmcollection.model.Film
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class FilmCollectionViewModel(
    private val repository: FilmRepository,
    private val autoRefreshIntervalMillis: Long = DEFAULT_AUTO_REFRESH_INTERVAL_MILLIS,
) : ViewModel() {
    val films: StateFlow<List<Film>> = repository.films

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _snackbarChannel = Channel<String>(Channel.BUFFERED)
    val snackbarEvents: Flow<String> = _snackbarChannel.receiveAsFlow()

    private val _refreshErrors = Channel<Unit>(Channel.CONFLATED)
    val refreshErrors: Flow<Unit> = _refreshErrors.receiveAsFlow()

    private val refreshMutex = Mutex()
    private var autoRefreshJob: Job? = null

    init {
        refresh()
        startAutoRefresh()
    }

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

    fun deleteFilm(id: String) {
        repository.deleteFilm(id)
    }

    fun refresh() {
        viewModelScope.launch {
            performRefresh()
        }
    }

    fun showSnackbar(message: String) {
        _snackbarChannel.trySend(message)
    }

    private suspend fun performRefresh() {
        if (!refreshMutex.tryLock()) return
        try {
            _isRefreshing.value = true
            repository.refresh()
                .onFailure { _refreshErrors.trySend(Unit) }
        } finally {
            _isRefreshing.value = false
            refreshMutex.unlock()
        }
    }

    private fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(autoRefreshIntervalMillis)
                performRefresh()
            }
        }
    }

    companion object {
        private const val DEFAULT_AUTO_REFRESH_INTERVAL_MILLIS: Long = 60_000L
    }
}

package com.example.filmcollection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.filmcollection.data.FilmRepository

class FilmCollectionViewModelFactory(
    private val repository: FilmRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(FilmCollectionViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return FilmCollectionViewModel(repository) as T
    }
}

package com.example.filmcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.filmcollection.data.FilmRepository
import com.example.filmcollection.data.InMemoryFilmRepository
import com.example.filmcollection.ui.filmlist.FilmListScreen
import com.example.filmcollection.ui.theme.FilmCollectionTheme
import com.example.filmcollection.viewmodel.FilmCollectionViewModel
import com.example.filmcollection.viewmodel.FilmCollectionViewModelFactory

class MainActivity : ComponentActivity() {
    private val repository: FilmRepository by lazy {
        InMemoryFilmRepository()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val filmCollectionViewModel: FilmCollectionViewModel = viewModel(
                factory = FilmCollectionViewModelFactory(repository),
            )
            FilmCollectionTheme {
                FilmListScreen(viewModel = filmCollectionViewModel)
            }
        }
    }
}

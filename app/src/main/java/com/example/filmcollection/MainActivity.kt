package com.example.filmcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.filmcollection.data.FilmRepository
import com.example.filmcollection.data.InMemoryFilmRepository
import com.example.filmcollection.data.remote.RetrofitFilmRemoteDataSource
import com.example.filmcollection.navigation.FilmNavHost
import com.example.filmcollection.network.NetworkModule
import com.example.filmcollection.ui.theme.FilmCollectionTheme
import com.example.filmcollection.viewmodel.FilmCollectionViewModel
import com.example.filmcollection.viewmodel.FilmCollectionViewModelFactory
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val repository: FilmRepository by lazy {
        InMemoryFilmRepository(
            remoteDataSource = RetrofitFilmRemoteDataSource(NetworkModule.filmApi),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FilmCollectionViewModel = viewModel(
                factory = FilmCollectionViewModelFactory(repository),
            )
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                viewModel.snackbarEvents.collectLatest { message ->
                    snackbarHostState.showSnackbar(message)
                }
            }

            FilmCollectionTheme {
                FilmNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}

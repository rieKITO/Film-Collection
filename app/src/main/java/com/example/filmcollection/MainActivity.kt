package com.example.filmcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.filmcollection.navigation.FilmNavHost
import com.example.filmcollection.ui.theme.FilmCollectionTheme
import com.example.filmcollection.viewmodel.FilmCollectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FilmCollectionViewModel = hiltViewModel()
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

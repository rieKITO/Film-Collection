package com.example.filmcollection.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.filmcollection.ui.filmdetail.FilmDetailScreen
import com.example.filmcollection.ui.filmlist.FilmListScreen
import com.example.filmcollection.viewmodel.FilmCollectionViewModel

@Composable
fun FilmNavHost(
    navController: NavHostController,
    viewModel: FilmCollectionViewModel,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.FilmList.route,
    ) {
        composable(Screen.FilmList.route) {
            FilmListScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onFilmClick = { filmId ->
                    navController.navigate(Screen.FilmDetail.createRoute(filmId))
                },
            )
        }
        composable(
            route = Screen.FilmDetail.route,
            arguments = listOf(
                navArgument(Screen.FilmDetail.ARG_FILM_ID) { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getLong(Screen.FilmDetail.ARG_FILM_ID)
                ?: return@composable
            FilmDetailScreen(
                filmId = filmId,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

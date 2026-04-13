package com.example.filmcollection.navigation

sealed class Screen(val route: String) {
    data object FilmList : Screen("film_list")
    data object FilmDetail : Screen("film_detail/{filmId}") {
        const val ARG_FILM_ID = "filmId"
        fun createRoute(filmId: Long) = "film_detail/$filmId"
    }
}

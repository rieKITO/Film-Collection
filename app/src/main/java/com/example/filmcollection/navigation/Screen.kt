package com.example.filmcollection.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object FilmList : Screen("film_list")
    data object FilmDetail : Screen("film_detail/{filmId}") {
        const val ARG_FILM_ID = "filmId"
        fun createRoute(filmId: String): String = "film_detail/${Uri.encode(filmId)}"
    }
}

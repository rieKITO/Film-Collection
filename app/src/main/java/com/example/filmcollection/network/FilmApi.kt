package com.example.filmcollection.network

import com.example.filmcollection.network.dto.MoviesResponse
import retrofit2.http.GET

interface FilmApi {
    @GET("movies")
    suspend fun getMovies(): MoviesResponse
}

package com.example.filmcollection.network.dto

import com.google.gson.annotations.SerializedName

data class MoviesResponse(
    @SerializedName("movies") val movies: List<MovieDto>? = null,
)

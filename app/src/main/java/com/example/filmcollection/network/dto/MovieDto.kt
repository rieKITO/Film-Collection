package com.example.filmcollection.network.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("year") val year: Int?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("director") val director: String?,
    @SerializedName("updatedAt") val updatedAt: String? = null,
)

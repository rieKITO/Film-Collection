package com.example.filmcollection.model

data class Film(
    val id: Long,
    val title: String,
    val year: Int,
    val genre: String,
    val country: String,
    val director: String,
    val rating: Int? = null,
    val watchStatus: WatchStatus = WatchStatus.NOT_WATCHED,
    val imageResId: Int? = null,
)

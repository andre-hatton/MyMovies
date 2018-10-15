package com.yoshizuka.mymovies.models.entities

import com.google.gson.annotations.SerializedName

data class Movie (
    val id: Int,
    val name: String = "",
    val overview: String = "",
    @SerializedName("media_type")
    val mediaType: String = "",
    val title: String = "",
    @SerializedName("poster_path")
    val posterPath: String = "",
    @SerializedName("backdrop_path")
    val backdropPath: String = ""
)

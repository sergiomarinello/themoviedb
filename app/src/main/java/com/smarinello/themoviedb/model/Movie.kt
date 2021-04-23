package com.smarinello.themoviedb.model

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the [Movie] returned from server.
 */
data class Movie(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("poster_path")
    val posterUrl: String,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("genre_ids")
    val genreIds: List<Int>,

    @SerializedName("backdrop_path")
    val backdropPath: String,

    var genres: String,

    var isFavorite: Boolean
)

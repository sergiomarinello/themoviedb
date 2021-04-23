package com.smarinello.themoviedb.model

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the [MovieResponse] returned from server.
 */
data class MovieResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<Movie>,

    @SerializedName("total_results")
    val totalResults: Int,

    @SerializedName("total_pages")
    val totalPages: Int
)

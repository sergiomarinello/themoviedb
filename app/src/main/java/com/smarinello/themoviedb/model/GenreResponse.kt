package com.smarinello.themoviedb.model

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents the [GenreResponse] returned from server.
 */
data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)

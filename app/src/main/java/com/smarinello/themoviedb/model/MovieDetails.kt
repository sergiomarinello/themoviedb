package com.smarinello.themoviedb.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.smarinello.themoviedb.repository.local.Converters

/**
 * Data class that represents the [MovieDetails] returned from server.
 */
@Entity(tableName = "movie_detail")
@TypeConverters(Converters::class)
data class MovieDetails(
    @SerializedName("id")
    @PrimaryKey val id: Int,

    @SerializedName("backdrop_path")
    val backdropPath: String,

    @SerializedName("genres")
    val genres: List<Genre>,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("revenue")
    val revenue: Long,

    @SerializedName("runtime")
    val runtime: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("poster_path")
    val posterUrl: String,

    @SerializedName("homepage")
    val homepage: String,

    var isFavorite: Boolean
) {
    companion object {
        const val EXTRA_MOVIE_ID = "movie_id"
        const val INVALID_MOVIE_ID: Int = -1
    }
}

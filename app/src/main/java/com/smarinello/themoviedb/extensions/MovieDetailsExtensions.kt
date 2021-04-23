package com.smarinello.themoviedb.extensions

import android.content.Context
import com.smarinello.themoviedb.BuildConfig
import com.smarinello.themoviedb.model.Movie
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.repository.Repository
import java.text.NumberFormat
import java.util.Currency
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * Get poster URL.
 */
fun MovieDetails.buildPosterUrl(): String = BuildConfig.POSTER_BASE_URL + posterUrl

/**
 * Get all genres names.
 */
private fun MovieDetails.allGenresName(): String =
    this.genres.stream().map { it.name }.collect(Collectors.joining(", "))

/**
 * Get summary of the movies: time and genres.
 */
fun MovieDetails.summary(): String {
    val hours = TimeUnit.MINUTES.toHours(runtime.toLong())
    val minutes = runtime - TimeUnit.HOURS.toMinutes(hours)
    val timeString = if (runtime < TimeUnit.HOURS.toMinutes(1)) "${runtime}m" else "${hours}h ${minutes}m"
    return "$timeString | ${allGenresName()}"
}

/**
 * Create an instance of [Movie] from a [MovieDetails].
 */
fun MovieDetails.createMovie(): Movie {
    val genres = this.allGenresName()
    val genreIds = this.genres.stream().map { it.id }.collect(Collectors.toList())
    return Movie(id, title, posterUrl, releaseDate, genreIds, backdropPath, genres, isFavorite)
}

/**
 * Create a list of [Movie] from a [MovieDetails] one.
 */
fun List<MovieDetails>.createMovieList(): List<Movie> =
    this.stream().map { it.createMovie() }.collect(Collectors.toList())

/**
 * Create a the revenue value from a [MovieDetails] with the local format.
 */
fun MovieDetails.convertRevenue(context: Context?): String {
    val currentLocale = context?.resources?.configuration?.locales?.get(0) ?: Repository.defaultLocale
    val format = NumberFormat.getCurrencyInstance(currentLocale)
    format.currency = Currency.getInstance(currentLocale)
    return if (this.revenue > 0) format.format((this.revenue)) else ""
}

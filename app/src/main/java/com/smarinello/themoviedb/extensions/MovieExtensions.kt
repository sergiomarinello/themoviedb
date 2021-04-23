package com.smarinello.themoviedb.extensions

import com.smarinello.themoviedb.BuildConfig
import com.smarinello.themoviedb.model.Movie

/**
 * Get backdrop URL from a movie
 */
fun Movie.buildBackdropUrl(): String = BuildConfig.BACKDROP_BASE_URL + backdropPath

/**
 * Get backdrop URL from a movie
 */
fun Movie.buildPosterUrl(): String = BuildConfig.POSTER_BASE_URL + posterUrl

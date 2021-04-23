package com.smarinello.themoviedb.repository

import com.smarinello.themoviedb.model.Genre
import com.smarinello.themoviedb.model.Movie
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.repository.local.AppDatabase
import com.smarinello.themoviedb.repository.remote.MovieRepository
import com.smarinello.themoviedb.repository.remote.ResponseResult
import com.smarinello.themoviedb.utils.ConnectivityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import java.util.Locale

/**
 * Access data.
 */
class Repository(
    private val movieRepository: MovieRepository,
    private val local: AppDatabase,
    private val connectivityUtils: ConnectivityUtils
) {

    /**
     * Get popular movies with in the [language] in the [page].
     */
    suspend fun getPopular(
        page: Int = 1,
        language: String = DEFAULT_LOCALE_STRING
    ): ResponseResult<List<Movie>> =
        if (connectivityUtils.isInternetAccessAvailable()) movieRepository.getPopular(page, language) else
            ResponseResult.Error(UnknownHostException())

    /**
     * Search movies with [query] string in the [language] with number of [page].
     */
    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        language: String = DEFAULT_LOCALE_STRING,
    ): ResponseResult<List<Movie>> =
        if (connectivityUtils.isInternetAccessAvailable()) movieRepository.searchMovies(query, language, page) else
            ResponseResult.Error(UnknownHostException())

    /**
     * Get genre list with in the [language].
     */
    suspend fun getGenreList(language: String = DEFAULT_LOCALE_STRING): List<Genre> {
        return if (connectivityUtils.isInternetAccessAvailable()) {
            val result: ResponseResult<List<Genre>> = withContext(Dispatchers.IO) {
                movieRepository.getGenreList(language)
            }
            if (result is ResponseResult.Success) {
                result.data
            } else {
                listOf()
            }
        } else {
            listOf()
        }
    }

    /**
     * Get movie detail with [id] in the [language].
     */
    suspend fun getMovieDetails(
        id: Int,
        language: String = DEFAULT_LOCALE_STRING
    ): ResponseResult<MovieDetails> =
        if (connectivityUtils.isInternetAccessAvailable()) movieRepository.getMovieDetail(id, language) else
            ResponseResult.Success(local.detailsDao().getMovieDetails(id))

    /**
     * Save [movieDetails] as favorite.
     */
    suspend fun saveFavoriteMovieDetails(movieDetails: MovieDetails) =
        local.detailsDao().insertMovieDetails(movieDetails)

    /**
     * Remove [MovieDetails] as favorite by its [id].
     */
    suspend fun deleteFavoriteMovieDetails(id: Int) = local.detailsDao().deleteMovieDetails(id)

    /**
     * Get the favorite [MovieDetails] list.
     */
    suspend fun getFavoriteMovieDetailsList(): List<MovieDetails> = local.favoriteDao().getFavoriteMovieDetailsList()

    /**
     * Get [MovieDetails] as favorite by its [id].
     */
    suspend fun getFavoriteMovieDetails(id: Int): MovieDetails? = local.favoriteDao().getFavoriteMovieDetails(id)

    /**
     * Remove a [MovieDetails] from [id] in favorites.
     */
    suspend fun removeFavoriteMovie(id: Int) = local.favoriteDao().deleteFavoriteMovie(id)

    companion object {
        /**
         * Define the default locale.
         */
        const val DEFAULT_LOCALE_STRING = "en-US"

        /**
         * Default locale.
         */
        val defaultLocale = Locale.US
    }
}

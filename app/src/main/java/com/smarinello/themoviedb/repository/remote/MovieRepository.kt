package com.smarinello.themoviedb.repository.remote

import com.smarinello.themoviedb.model.Genre
import com.smarinello.themoviedb.model.Movie
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.repository.Repository

/**
 * Abstract data repository from server.
 */
@Suppress("TooGenericExceptionCaught")
class MovieRepository(private val remoteAccess: RemoteAccess) {

    /**
     * Get popular movies with in the [language] in the [page].
     */
    suspend fun getPopular(
        page: Int = 1,
        language: String = Repository.DEFAULT_LOCALE_STRING
    ): ResponseResult<List<Movie>> {
        return try {
            val result = createEndpoint().getPopular(page, language)
            ResponseResult.Success(result.results)
        } catch (ex: Exception) {
            ResponseResult.Error(ex)
        }
    }

    /**
     * Search movies with [query] string in the [language] with number of [page].
     */
    suspend fun searchMovies(
        query: String,
        language: String = Repository.DEFAULT_LOCALE_STRING,
        page: Int = 1
    ): ResponseResult<List<Movie>> {
        return try {
            val result = createEndpoint().searchMovies(query, language, page)
            ResponseResult.Success(result.results)
        } catch (ex: Exception) {
            ResponseResult.Error(ex)
        }
    }

    /**
     * Get genre list with in the [language].
     */
    suspend fun getGenreList(language: String = Repository.DEFAULT_LOCALE_STRING): ResponseResult<List<Genre>> {
        return try {
            val result = createEndpoint().getGenreList(language)
            ResponseResult.Success(result.genres)
        } catch (ex: Exception) {
            ResponseResult.Error(ex)
        }
    }

    /**
     * Get movie detail with [id] in the [language].
     */
    suspend fun getMovieDetail(
        id: Int,
        language: String = Repository.DEFAULT_LOCALE_STRING
    ): ResponseResult<MovieDetails> {
        return try {
            val result = createEndpoint().getMovieDetail(id, language)
            ResponseResult.Success(result)
        } catch (ex: Exception) {
            ResponseResult.Error(ex)
        }
    }

    private fun createEndpoint() = remoteAccess.retrofit.create(MovieEndpoint::class.java)
}

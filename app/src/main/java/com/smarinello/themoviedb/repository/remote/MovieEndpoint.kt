package com.smarinello.themoviedb.repository.remote

import com.smarinello.themoviedb.model.GenreResponse
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Endpoint that requests the data from the movies to the server.
 */
interface MovieEndpoint {

    @GET("/3/movie/popular")
    suspend fun getPopular(
        @Query("page") page: Int,
        @Query("language") language: String
    ): MovieResponse

    @GET("/3/search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): MovieResponse

    @GET("/3/genre/movie/list")
    suspend fun getGenreList(
        @Query("language") language: String
    ): GenreResponse

    @GET("/3/movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") id: Int,
        @Query("language") language: String
    ): MovieDetails
}

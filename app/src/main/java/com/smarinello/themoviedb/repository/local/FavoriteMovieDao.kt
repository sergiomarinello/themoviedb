package com.smarinello.themoviedb.repository.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smarinello.themoviedb.model.MovieDetails

/**
 * Favorite movie DAO.
 */
@Dao
interface FavoriteMovieDao {

    @Query("SELECT * FROM movie_detail ORDER BY title")
    suspend fun getFavoriteMovieDetailsList(): List<MovieDetails>

    @Query("SELECT * FROM movie_detail WHERE id = :id")
    suspend fun getFavoriteMovieDetails(id: Int): MovieDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMovieDetails(movieDetails: MovieDetails)

    @Query("DELETE FROM movie_detail WHERE id = :id")
    suspend fun deleteFavoriteMovie(id: Int)

    @Delete
    suspend fun deleteFavoriteMovie(movieDetails: MovieDetails)

    @Query("SELECT * FROM movie_detail WHERE title LIKE '%' || :query  || '%' ORDER BY title")
    suspend fun searchMovies(query: String): List<MovieDetails>?
}

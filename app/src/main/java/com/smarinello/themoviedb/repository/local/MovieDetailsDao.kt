package com.smarinello.themoviedb.repository.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smarinello.themoviedb.model.MovieDetails

/**
 * Movie details DAO.
 */
@Dao
interface MovieDetailsDao {

    @Query("SELECT * FROM movie_detail")
    suspend fun getMovieDetailsList(): List<MovieDetails>

    @Query("SELECT * FROM movie_detail WHERE id = :id")
    suspend fun getMovieDetails(id: Int): MovieDetails

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(movieDetails: MovieDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMovieDetails(movieDetails: MovieDetails)

    @Query("DELETE FROM movie_detail WHERE id = :id")
    suspend fun deleteMovieDetails(id: Int)

    @Delete
    suspend fun deleteMovieDetails(movieDetails: MovieDetails)
}

package com.smarinello.themoviedb.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smarinello.themoviedb.model.Genre

/**
 * Genres DAO.
 */
@Dao
interface GenresDao {

    @Query("SELECT * FROM genre_list")
    suspend fun getGenresList(): List<Genre>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreList(list: List<Genre>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: Genre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGenreList(list: List<Genre>)

    @Query("DELETE FROM genre_list")
    suspend fun deleteGenresList()
}

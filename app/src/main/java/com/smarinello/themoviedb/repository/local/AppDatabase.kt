package com.smarinello.themoviedb.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smarinello.themoviedb.model.Genre
import com.smarinello.themoviedb.model.MovieDetails

/**
 * Room database.
 */
@Database(
    entities = [MovieDetails::class, Genre::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteMovieDao
    abstract fun detailsDao(): MovieDetailsDao
    abstract fun genresDao(): GenresDao
}

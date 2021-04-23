package com.smarinello.themoviedb

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.smarinello.themoviedb.model.Genre
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.repository.local.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DatabaseTest {
    private lateinit var context: Context
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Test
    fun `add and remove a movie to the detail database by instance`() = runBlocking {
        // Assert that the database is clear
        Assert.assertEquals(database.detailsDao().getMovieDetailsList().size, 0)
        // Add a new detail entry
        val movieDetails = MovieDetails(
            1, "backdropUrl1",
            arrayListOf(Genre(id = 1, name = "Genre1")), "Overview1",
            "releaseDate1", 1000, 180, "Title1", "posterUrl1",
            "homepage1", false
        )
        database.detailsDao().insertMovieDetails(movieDetails)
        Assert.assertEquals(database.detailsDao().getMovieDetailsList().size, 1)
        Assert.assertEquals(database.detailsDao().getMovieDetailsList()[0], movieDetails)
        // Delete detail entry
        database.detailsDao().deleteMovieDetails(movieDetails)
        // Assert that the database is clear
        Assert.assertEquals(database.detailsDao().getMovieDetailsList().size, 0)
    }

    @Test
    fun `add a movie by instance and remove by its id to the detail database`() = runBlocking {
        // Assert that the database is clear
        Assert.assertEquals(database.detailsDao().getMovieDetailsList().size, 0)
        // Add a new detail entry
        val movieDetails = MovieDetails(
            1, "backdropUrl1",
            arrayListOf(Genre(id = 1, name = "Genre1")), "Overview1",
            "releaseDate1", 1000, 180, "Title1", "posterUrl1",
            "homepage1", false
        )
        database.detailsDao().insertMovieDetails(movieDetails)
        Assert.assertEquals(database.detailsDao().getMovieDetailsList().size, 1)
        Assert.assertEquals(database.detailsDao().getMovieDetailsList()[0], movieDetails)
        // Delete detail entry
        database.detailsDao().deleteMovieDetails(movieDetails.id)
        // Assert that the database is clear
        Assert.assertEquals(database.detailsDao().getMovieDetailsList().size, 0)
    }

    @Test
    fun `add and remove a movie to the favorite database by instance`() = runBlocking {
        // Assert that the database is clear
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList().size, 0)
        // Add a new detail entry
        val movieDetails = MovieDetails(
            1, "backdropUrl1",
            arrayListOf(Genre(id = 1, name = "Genre1")), "Overview1",
            "releaseDate1", 1000, 180, "Title1", "posterUrl1",
            "homepage1", false
        )
        database.favoriteDao().insertFavoriteMovieDetails(movieDetails)
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList().size, 1)
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList()[0], movieDetails)
        // Delete detail entry
        database.favoriteDao().deleteFavoriteMovie(movieDetails)
        // Assert that the database is clear
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList().size, 0)
    }

    @Test
    fun `add a movie by instance and remove by its id to the favorite database`() = runBlocking {
        // Assert that the database is clear
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList().size, 0)
        // Add a new detail entry
        val movieDetails = MovieDetails(
            1, "backdropUrl1",
            arrayListOf(Genre(id = 1, name = "Genre1")), "Overview1",
            "releaseDate1", 1000, 180, "Title1", "posterUrl1",
            "homepage1", false
        )
        database.favoriteDao().insertFavoriteMovieDetails(movieDetails)
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList().size, 1)
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList()[0], movieDetails)
        // Delete detail entry
        database.favoriteDao().deleteFavoriteMovie(movieDetails.id)
        // Assert that the database is clear
        Assert.assertEquals(database.favoriteDao().getFavoriteMovieDetailsList().size, 0)
    }

    @After
    fun tearDown() {
        database.close()
        stopKoin()
    }
}

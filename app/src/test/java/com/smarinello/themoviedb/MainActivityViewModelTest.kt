package com.smarinello.themoviedb

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.smarinello.themoviedb.model.Genre
import com.smarinello.themoviedb.model.Movie
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.repository.Repository
import com.smarinello.themoviedb.repository.remote.ResponseResult
import com.smarinello.themoviedb.utils.ConnectivityUtils
import com.smarinello.themoviedb.viewmodel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.stopKoin
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.lang.ref.WeakReference

class MainActivityViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var connectivityUtils: ConnectivityUtils

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: MainActivityViewModel
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private var popularMoviesListPage1 = arrayListOf<Movie>(createMovie(1), createMovie(2))
    private var popularMoviesListPage2 = arrayListOf<Movie>(createMovie(3), createMovie(4))
    private var favoriteMovieDetails = arrayListOf<MovieDetails>(createMovieDetail(2, true))
    private var favoriteMovies = arrayListOf<Movie>(createMovie(2, true))
    private var searchMoviesPage1 = arrayListOf<Movie>(createMovie(2), createMovie(3))
    private var searchMoviesPage2 = arrayListOf<Movie>(createMovie(5), createMovie(6))

    // Observers
    @Mock
    private lateinit var observerOperation: Observer<MainActivityViewModel.OperationSelected>

    init {
        MockitoAnnotations.openMocks(this)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = MainActivityViewModel(repository, connectivityUtils, WeakReference(context))
        viewModel.apply {
            operationSelected.observeForever(observerOperation)
        }

        `when`(connectivityUtils.isInternetAccessAvailable()).thenReturn(true)
        runBlocking {
            `when`(repository.getPopular(page = 1)).doReturn(ResponseResult.Success(popularMoviesListPage1))
            `when`(repository.getPopular(page = 2)).doReturn(ResponseResult.Success(popularMoviesListPage2))
            `when`(repository.getGenreList()).doReturn(createGenreList(6))
            `when`(repository.getFavoriteMovieDetailsList()).doReturn(favoriteMovieDetails.toList())
            `when`(repository.searchMovies(query = "Title", page = 1)).doReturn(
                ResponseResult.Success(searchMoviesPage1)
            )
            `when`(repository.searchMovies(query = "Title", page = 2)).doReturn(
                ResponseResult.Success(searchMoviesPage2)
            )
        }
    }

    @Test
    fun `When popular chip was clicked, user receives the 1st page and asks another one`() {
        viewModel.onPopularSelected()
        Thread.sleep(500)
        assertThat(viewModel.operationSelected.value).isEqualTo(MainActivityViewModel.OperationSelected.POPULAR)
        assertThat(viewModel.movieList.value).isEqualTo(popularMoviesListPage1)

        // Needs to load another page
        viewModel.loadAnotherPage()
        Thread.sleep(500)

        // The list of movies when gets the 2nd page has popularMoviesListPage1+popularMoviesListPage2
        val newList: ArrayList<Movie> = ArrayList(popularMoviesListPage1)
        for (movie in popularMoviesListPage2) {
            newList.add(movie)
        }

        assertThat(viewModel.movieList.value).isEqualTo(newList)
    }

    @Test
    fun `When favorite chip was clicked and it returns the favorite movie list`() {
        viewModel.onFavoriteSelected()
        Thread.sleep(500)
        assertThat(viewModel.operationSelected.value).isEqualTo(MainActivityViewModel.OperationSelected.FAVORITE)
        assertThat(viewModel.movieList.value).isEqualTo(favoriteMovies)
    }

    @Test
    fun `When popular chip was submit, user receives the 1st page and asks another one`() {
        viewModel.searchSubmit("Title")
        Thread.sleep(500)
        assertThat(viewModel.operationSelected.value).isEqualTo(MainActivityViewModel.OperationSelected.SEARCH)
        assertThat(viewModel.movieList.value).isEqualTo(searchMoviesPage1)

        // Needs to load another page
        viewModel.loadAnotherPage()
        Thread.sleep(500)

        // The list of movies when gets the 2nd page has searchMoviesPage1+searchMoviesPage2
        val newList: ArrayList<Movie> = ArrayList(searchMoviesPage1)
        for (movie in searchMoviesPage2) {
            newList.add(movie)
        }

        assertThat(viewModel.movieList.value).isEqualTo(newList)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    private fun createMovieDetail(index: Int, isFavorite: Boolean = false): MovieDetails {
        return MovieDetails(
            index, "backdropUrl$index",
            arrayListOf(Genre(id = index, name = "Genre$index")), "overview1",
            "releaseDate$index", 1000L * index, 60 * index, "Title$index", "posterUrl$index",
            "homepage$index", isFavorite
        )
    }

    private fun createMovie(index: Int, isFavorite: Boolean = false): Movie {
        return Movie(
            index, "Title$index", "posterUrl$index",
            "releaseDate$index", listOf(index), "backdropUrl$index",
            "Genre$index", isFavorite
        )
    }

    private fun createGenreList(index: Int): List<Genre> {
        val genreList = mutableListOf<Genre>()
        for (i in 1 until index) {
            genreList.add(Genre(i, "Genre$i"))
        }
        return genreList.toList()
    }
}

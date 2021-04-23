package com.smarinello.themoviedb

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.smarinello.themoviedb.extensions.buildPosterUrl
import com.smarinello.themoviedb.extensions.convertRevenue
import com.smarinello.themoviedb.extensions.summary
import com.smarinello.themoviedb.model.Genre
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.repository.Repository
import com.smarinello.themoviedb.repository.remote.ResponseResult
import com.smarinello.themoviedb.utils.ConnectivityUtils
import com.smarinello.themoviedb.viewmodel.DetailActivityViewModel
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

class DetailViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var connectivityUtils: ConnectivityUtils

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: DetailActivityViewModel
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val movieIndex = 1
    private val movieDetails = createMovieDetail(movieIndex, false)

    // Observers
    @Mock
    private lateinit var observerString: Observer<String>

    init {
        MockitoAnnotations.openMocks(this)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = DetailActivityViewModel(repository, connectivityUtils, WeakReference(context))
        runBlocking {
            viewModel.apply {
                title.observeForever(observerString)
                releaseDate.observeForever(observerString)
                summary.observeForever(observerString)
                revenue.observeForever(observerString)
                overview.observeForever(observerString)
                posterUrl.observeForever(observerString)
                homepage.observeForever(observerString)
            }
            `when`(connectivityUtils.isInternetAccessAvailable()).thenReturn(true)
            `when`(repository.getMovieDetails(movieIndex)).doReturn(ResponseResult.Success(movieDetails))
            `when`(repository.getFavoriteMovieDetails(movieIndex)).doReturn(movieDetails)
        }
    }

    @Test
    fun `When enter in the activity, verifies if the title is requested and its value is returned correctly`() {
        viewModel.start(movieIndex)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(movieDetails)
        assertThat(viewModel.favoriteButtonVisibility.value).isTrue()
        assertThat(viewModel.title.value).isEqualTo(movieDetails.title)
    }

    @Test
    fun `When enter in the activity, verifies if the release date is requested and its value is returned correctly`() {
        viewModel.start(movieIndex)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(movieDetails)
        assertThat(viewModel.favoriteButtonVisibility.value).isTrue()
        assertThat(viewModel.releaseDate.value).isEqualTo(movieDetails.releaseDate)
    }

    @Test
    fun `When enter in the activity, verifies if the genres are requested and their value is returned correctly`() {
        viewModel.start(movieIndex)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(movieDetails)
        assertThat(viewModel.favoriteButtonVisibility.value).isTrue()
        assertThat(viewModel.summary.value).isEqualTo(movieDetails.summary())
    }

    @Test
    fun `When enter in the activity, verifies if the revenue is requested and its value is returned correctly`() {
        viewModel.start(movieIndex)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(movieDetails)
        assertThat(viewModel.favoriteButtonVisibility.value).isTrue()
        assertThat(viewModel.revenue.value).isEqualTo(movieDetails.convertRevenue(context))
    }

    @Test
    fun `When enter in the activity, verifies if the overview is requested and its value is returned correctly`() {
        viewModel.start(movieIndex)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(movieDetails)
        assertThat(viewModel.favoriteButtonVisibility.value).isTrue()
        assertThat(viewModel.overview.value).isEqualTo(movieDetails.overview)
    }

    @Test
    fun `When enter in the activity, verifies if the poster url is requested and its value is returned correctly`() {
        viewModel.start(movieIndex)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(movieDetails)
        assertThat(viewModel.favoriteButtonVisibility.value).isTrue()
        assertThat(viewModel.posterUrl.value).isEqualTo(movieDetails.buildPosterUrl())
    }

    @Test
    fun `When enter in the activity, verifies if the homepage is requested and its value is returned correctly`() {
        viewModel.start(movieIndex)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(movieDetails)
        assertThat(viewModel.favoriteButtonVisibility.value).isTrue()
        assertThat(viewModel.homepage.value).isEqualTo(movieDetails.homepage)
    }

    @Test
    fun `An invalid movie id is passed through the intent the values should be the same as the initialization`() {
        viewModel.start(MovieDetails.INVALID_MOVIE_ID)
        Thread.sleep(500)
        assertThat(viewModel.movieDetailsLiveData.value).isNotEqualTo(movieDetails)
        assertThat(viewModel.movieDetailsLiveData.value).isEqualTo(null)
        assertThat(viewModel.favoriteButtonVisibility.value).isFalse()
    }

    private fun createMovieDetail(index: Int, isFavorite: Boolean = false): MovieDetails {
        return MovieDetails(
            index, "backdropUrl$index",
            arrayListOf(Genre(id = index, name = "Genre$index")), "overview1",
            "releaseDate$index", 1000L * index, 60 * index, "title$index", "posterUrl$index",
            "homepage$index", isFavorite
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}

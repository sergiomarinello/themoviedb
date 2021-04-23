package com.smarinello.themoviedb.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.smarinello.themoviedb.R
import com.smarinello.themoviedb.extensions.buildPosterUrl
import com.smarinello.themoviedb.extensions.convertRevenue
import com.smarinello.themoviedb.extensions.createMovie
import com.smarinello.themoviedb.extensions.summary
import com.smarinello.themoviedb.model.Movie
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.repository.Repository
import com.smarinello.themoviedb.repository.remote.ResponseResult
import com.smarinello.themoviedb.utils.ConnectivityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/**
 * [AndroidViewModel] used to provide the content to the [DetailActivityViewModel].
 */
class DetailActivityViewModel(
    private val repository: Repository,
    private val connectivityUtils: ConnectivityUtils,
    private val weakContext: WeakReference<Context>
) : ViewModel(), CoroutineScope {
    private val tag = this::class.java.simpleName
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    var movieDetailsLiveData = MutableLiveData<MovieDetails>()

    var title: LiveData<String> = Transformations.map(movieDetailsLiveData) { it.title }
    var summary: LiveData<String> = Transformations.map(movieDetailsLiveData) { it.summary() }
    var releaseDate: LiveData<String> = Transformations.map(movieDetailsLiveData) { it.releaseDate }
    var revenue: LiveData<String> = Transformations.map(movieDetailsLiveData) { it.convertRevenue(weakContext.get()) }
    var overview: LiveData<String> = Transformations.map(movieDetailsLiveData) { it.overview }
    var posterUrl: LiveData<String> = Transformations.map(movieDetailsLiveData) { it.buildPosterUrl() }
    var homepage: LiveData<String> = Transformations.map(movieDetailsLiveData) { it.homepage }

    var showToastMessage = MutableLiveData<Boolean>(false)
    var toastMessage = MutableLiveData<String>("")
    var showDetailsLoadingProgressBar = MutableLiveData<Boolean>(false)
    var favoriteButtonVisibility = MutableLiveData<Boolean>(false)
    var isFavoriteStatus = MutableLiveData<Boolean>(false)

    private var favoriteMovie: Movie? = null
    private var actualMovieId: Int = MovieDetails.INVALID_MOVIE_ID

    /**
     * Init the [ViewModel] loading, requesting the [MovieDetails] with [movieId].
     */
    fun start(movieId: Int) {
        if (movieId == MovieDetails.INVALID_MOVIE_ID) return
        actualMovieId = movieId
        favoriteButtonVisibility.postValue(connectivityUtils.isInternetAccessAvailable())
        loadMovieDetails(movieId)
    }

    /**
     * Action when Favorite Button was clicked.
     */
    fun onClickFavoriteButton() {
        Log.d(tag, "onClickFavoriteButton, favoriteMovie = $favoriteMovie")
        val isInternetAccessAvailable = connectivityUtils.isInternetAccessAvailable()
        favoriteButtonVisibility.postValue(isInternetAccessAvailable)

        if (favoriteMovie != null) {
            Log.d(tag, "onClickFavoriteButton, favoriteMovie != null")
            removeMovieDetailsInRepository(actualMovieId)
            favoriteMovie = null
            isFavoriteStatus.value = false
        } else if (isInternetAccessAvailable) {
            Log.d(tag, "onClickFavoriteButton, internet is available")
            updateIsFavoriteStateInRepository(actualMovieId)
            favoriteMovie = movieDetailsLiveData.value?.createMovie()
            isFavoriteStatus.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    /**
     * Update Movie details with [id] as favorite in the repository.
     */
    private fun updateIsFavoriteStateInRepository(id: Int) {
        Log.d(tag, "updateIsFavoriteStateInRepository, id=$id")
        launch {
            val resultMovieDetail = withContext(Dispatchers.IO) { repository.getMovieDetails(id) }
            Log.d(tag, "updateIsFavoriteStateInRepository, resultMovieDetail = $resultMovieDetail")
            if (resultMovieDetail is ResponseResult.Success) {
                val actualMovie = resultMovieDetail.data
                actualMovie.isFavorite = true
                repository.saveFavoriteMovieDetails(actualMovie)
                Log.d(tag, "updateIsFavoriteStateInRepository, movie id = ${actualMovie.id}")
            }
        }
    }

    /**
     * Remove a Movie details with [id].
     */
    private fun removeMovieDetailsInRepository(id: Int) {
        Log.d(tag, "removeMovieDetailsInRepository, id=$id")
        launch {
            withContext(Dispatchers.IO) {
                repository.removeFavoriteMovie(id)
            }
        }
    }

    /**
     * Load a Movie details with [id].
     */
    private fun loadMovieDetails(id: Int) {
        Log.d(tag, "loadMovieDetails, id = $id")
        launch {
            showDetailsLoadingProgressBar.postValue(true)
            // Verify if the movie is a favorite one
            favoriteMovie = withContext(Dispatchers.IO) {
                repository.getFavoriteMovieDetails(id)?.createMovie()
            }
            val isFavorite = favoriteMovie?.id == id
            Log.d(tag, "loadMovieDetails, movieDetails id=$id, isFavorite = $isFavorite")
            isFavoriteStatus.value = isFavorite

            if (connectivityUtils.isInternetAccessAvailable() || isFavorite) {
                val result = withContext(Dispatchers.IO) {
                    repository.getMovieDetails(id = id)
                }
                showDetailsLoadingProgressBar.postValue(false)
                handleMovieListResult(result)
            } else {
                showDetailsLoadingProgressBar.postValue(false)
                showConnectivityToast()
            }
        }
    }

    /**
     * Handles the [ResponseResult] get from the api for a [MovieDetails].
     */
    private fun handleMovieListResult(result: ResponseResult<MovieDetails>) {
        when (result) {
            is ResponseResult.Success -> {
                val movieDetails = result.data
                onSuccessfulDataResult(movieDetails)
            }
            is ResponseResult.Error -> {
                Log.d(tag, "Error loading data: ${result.exception.message}")
                onErrorDataResult()
            }
        }
    }

    /**
     * Handle in case of the [ResponseResult.Success] get from the api for a [MovieDetails].
     */
    private fun onSuccessfulDataResult(movieDetails: MovieDetails) {
        Log.d(tag, "onSuccessfulDataResult, movieDetails = $movieDetails")
        movieDetailsLiveData.postValue(movieDetails)
    }

    /**
     * Handle in case of the [ResponseResult.Error] get a [MovieDetails]from the api.
     */
    private fun onErrorDataResult() {
        weakContext.get()?.let {
            toastMessage.postValue(it.resources.getString(R.string.error_fetch_data))
            showToastMessage.postValue(true)
        }
    }

    /**
     * Shows a [Toast] with connectivity error.
     */
    private fun showConnectivityToast() {
        weakContext.get()?.let {
            toastMessage.postValue(it.resources.getString(R.string.error_message_connectivity))
            showToastMessage.postValue(true)
        }
    }
}

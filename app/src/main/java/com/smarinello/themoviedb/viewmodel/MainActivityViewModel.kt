package com.smarinello.themoviedb.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smarinello.themoviedb.R
import com.smarinello.themoviedb.extensions.buildPosterUrl
import com.smarinello.themoviedb.extensions.createMovieList
import com.smarinello.themoviedb.model.Movie
import com.smarinello.themoviedb.repository.Repository
import com.smarinello.themoviedb.repository.remote.ResponseResult
import com.smarinello.themoviedb.utils.ConnectivityUtils
import com.smarinello.themoviedb.view.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.stream.Collectors
import kotlin.coroutines.CoroutineContext

private const val INVALID_MOVIE_INDEX: Int = -1

/**
 * [AndroidViewModel] used to provide the content to the [MainActivity].
 */
@Suppress("TooManyFunctions")
class MainActivityViewModel(
    private val repository: Repository,
    private val connectivityUtils: ConnectivityUtils,
    private val weakContext: WeakReference<Context>
) : ViewModel(),
    CoroutineScope,
    MainActivity.ScrollListener,
    MainActivity.FavoriteButton,
    MainActivity.DetailsInterface {
    private val tag = this::class.java.simpleName
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    private var genresMap = HashMap<String, String>()

    var actualPage = 1
    var searchQuery = ""

    var operationSelected = MutableLiveData<OperationSelected>(OperationSelected.NONE)
    var showToastMessage = MutableLiveData<Boolean>(false)
    var toastMessage = MutableLiveData<String>("")
    var showLoadingProgressBar = MutableLiveData<Boolean>(false)
    val movieList = MutableLiveData<ArrayList<Movie>>()

    /**
     * Init the
     */
    fun onInitialize() {
        actualPage = 1
        onPopularSelected()
        loadPopularPage(page = 1)
    }

    /**
     * Action executed when the "Popular" category was selected.
     */
    fun onPopularSelected() {
        if (connectivityUtils.isInternetAccessAvailable()) {
            Log.d(tag, "onPopularSelected")
            operationSelected.postValue(OperationSelected.POPULAR)
            actualPage = 1
            movieList.postValue(arrayListOf())
            populateGenres()
            loadPopularPage(page = 1)
        } else {
            movieList.postValue(arrayListOf())
            operationSelected.postValue(OperationSelected.NONE)
            showConnectivityToast()
        }
    }

    /**
     * Action executed when the "Favorite" category was selected.
     */
    fun onFavoriteSelected() {
        Log.d(tag, "onFavoriteSelected")
        operationSelected.postValue(OperationSelected.FAVORITE)
        actualPage = 1
        movieList.postValue(arrayListOf())
        populateGenres()
        loadFavorite()
    }

    /**
     * Action executed when the search was submitted.
     */
    fun searchSubmit(query: String?) {
        Log.d(tag, "searchSubmit, query = $query")
        operationSelected.postValue(OperationSelected.SEARCH)
        actualPage = 1
        populateGenres()
        movieList.postValue(arrayListOf())
        searchQuery = query.orEmpty()
        query?.let {
            loadSearch(query, actualPage)
        }
    }

    /**
     * Action executed when the favorite button was clicked.
     */
    override fun onClickFavoriteButton(movie: Movie) {
        val index: Int = movieList.value?.indexOf(movie) ?: INVALID_MOVIE_INDEX
        Log.d(tag, "onClickFavoriteButton, title = ${movie.title}, isFavorite = ${movie.isFavorite}, index = $index")
        movie.isFavorite = !movie.isFavorite
        if (index != INVALID_MOVIE_INDEX) {
            launch {
                if (movie.isFavorite && connectivityUtils.isInternetAccessAvailable()) {
                    val resultMovieDetail = withContext(Dispatchers.IO) { repository.getMovieDetails(movie.id) }
                    Log.d(tag, "onClickFavoriteButton, resultMovieDetail = $resultMovieDetail")
                    if (resultMovieDetail is ResponseResult.Success) {
                        val movieDetail = resultMovieDetail.data
                        movieDetail.isFavorite = true
                        withContext(Dispatchers.IO) {
                            repository.saveFavoriteMovieDetails(movieDetail)
                        }
                        Log.d(tag, "onClickFavoriteButton, saveFavoriteMovieDetails with index = ${movieDetail.id}")
                        cachePosterImage(movieDetail.buildPosterUrl())
                    }
                    updateMovieListOnClickFavoriteButton(movie, index)
                } else {
                    withContext(Dispatchers.IO) {
                        repository.removeFavoriteMovie(movie.id)
                        repository.deleteFavoriteMovieDetails(movie.id)
                        if (!connectivityUtils.isInternetAccessAvailable()) {
                            val newList = ArrayList<Movie>()
                            movieList.value?.removeAt(index)
                            movieList.value?.let {
                                newList.addAll(it)
                            }
                            movieList.postValue(newList)
                        } else {
                            updateMovieListOnClickFavoriteButton(movie, index)
                        }
                    }
                }
            }
        }
    }

    /**
     * Load another page, used for Search and Popular filters.
     */
    override fun loadAnotherPage() {
        if (operationSelected.value == OperationSelected.POPULAR) {
            Log.d(tag, "Load more popular movies")
            actualPage += 1
            loadPopularPage(actualPage)
        } else if (operationSelected.value == OperationSelected.SEARCH) {
            Log.d(tag, "Load more searched movies")
            actualPage += 1
            loadSearch(searchQuery, actualPage)
        }
    }

    /**
     * Change on the activity that comes back from the detail screen.
     */
    override fun onBackFromDetails() {
        /**
         * You can remove a movie on the "Favorite" inside Details screen, this needs to trigger an
         * update in the movie list.
         */
        Log.d(tag, "onBackFromDetails, operationSelected = ${operationSelected.value?.name}")
        Log.d(tag, "onBackFromDetails, movieList.value = ${movieList.value}")
        when (operationSelected.value) {
            OperationSelected.FAVORITE -> onFavoriteSelected()
            OperationSelected.POPULAR -> {
                val updatedList: List<Movie> = movieList.value ?: listOf()
                updateFavoriteStatus(updatedList)
                updateMovieList(updatedList)
                Log.d(tag, "onBackFromDetails, movieList.value = ${movieList.value}")
            }
            OperationSelected.SEARCH -> {
                val updatedList: List<Movie> = movieList.value ?: listOf()
                updateFavoriteStatus(updatedList)
                movieList.postValue(ArrayList(updatedList))
                Log.d(tag, "onBackFromDetails, movieList.value = ${movieList.value}")
            }
            else -> Log.d(tag, "onBackFromDetails, invalid option")
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    private suspend fun updateMovieListOnClickFavoriteButton(movie: Movie, index: Int) {
        when (operationSelected.value) {
            OperationSelected.FAVORITE -> {
                val favoriteMovieDetailsList = withContext(Dispatchers.IO) {
                    repository.getFavoriteMovieDetailsList()
                }
                movieList.postValue(ArrayList(favoriteMovieDetailsList.createMovieList()))
            }
            OperationSelected.SEARCH, OperationSelected.POPULAR -> {
                // Needs to create with movie updated to force a reload on the movie list.
                val newList = ArrayList<Movie>()
                movieList.value?.let {
                    newList.addAll(it)
                    newList[index] = movie
                }
                movieList.postValue(newList)
            }
            else -> Log.d(tag, "updateMovieListOnClickFavoriteButton, invalid option")
        }
    }

    /**
     * Cache the poster image from [posterUrl].
     */
    private suspend fun cachePosterImage(posterUrl: String) {
        weakContext.get()?.let {
            Log.d(tag, "onClickFavoriteButton, saving posterUrl = $posterUrl")
            withContext(Dispatchers.IO) {
                Glide.with(it)
                    .downloadOnly()
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .load(posterUrl)
                    .submit()
            }
        }
    }

    /**
     * Populate the [HashMap] of the genres with id as key.
     */
    private fun populateGenres() {
        if (genresMap.isEmpty()) {
            launch {
                showLoadingProgressBar.postValue(true)
                val result = withContext(Dispatchers.IO) {
                    repository.getGenreList()
                }
                if (result.isNotEmpty()) {
                    for (genre in result) {
                        genresMap[genre.id.toString()] = genre.name
                    }
                }
            }
        }
    }

    /**
     * Load a Popular [page].
     */
    private fun loadPopularPage(page: Int) {
        Log.d(tag, "loadPopular, page $page")
        if (connectivityUtils.isInternetAccessAvailable()) {
            showLoadingProgressBar.postValue(true)
            launch {
                val result = withContext(Dispatchers.IO) {
                    repository.getPopular(page)
                }
                showLoadingProgressBar.postValue(false)
                handleMovieListResult(result)
            }
        } else {
            showConnectivityToast()
        }
    }

    /**
     * Load the favorite movies.
     */
    private fun loadFavorite() {
        launch {
            showLoadingProgressBar.postValue(true)
            val result = withContext(Dispatchers.IO) { repository.getFavoriteMovieDetailsList() }
            showLoadingProgressBar.postValue(false)
            Log.d(tag, "Data retrieved with success, size = ${result.size}")
            onSuccessfulDataResult(result.createMovieList())
        }
    }

    private fun updateFavoriteStatus(list: List<Movie>) {
        launch {
            showLoadingProgressBar.postValue(true)
            val result = withContext(Dispatchers.IO) {
                repository.getFavoriteMovieDetailsList()
            }
            val resultIds = result.stream().map { it.id }.collect(Collectors.toList())
            for (movie in list) {
                movie.isFavorite = resultIds.contains(movie.id)
            }
            showLoadingProgressBar.postValue(false)
        }
    }

    private fun loadSearch(query: String?, page: Int) {
        if (!connectivityUtils.isInternetAccessAvailable()) {
            showConnectivityToast()
            movieList.postValue(arrayListOf())
            showSearchInFavoriteOnlyToast()
        }
        if (!query.isNullOrEmpty() && query.isNotBlank()) {
            showLoadingProgressBar.postValue(true)
            launch {
                val result = withContext(Dispatchers.IO) {
                    repository.searchMovies(
                        query,
                        page = page
                    )
                }
                showLoadingProgressBar.postValue(false)
                handleMovieListResult(result)
            }
        } else {
            movieList.postValue(arrayListOf())
        }
    }

    /**
     * Converts the genres int list of [movies] to a String withe movies genres.
     */
    private fun convertIntGenresToString(movies: List<Movie>) {
        if (genresMap.isNotEmpty()) {
            for (movie in movies) {
                val movieGenres = movie.genreIds
                    .stream()
                    .map { genresMap[it.toString()] }
                    .collect(Collectors.joining(", "))
                movie.genres = movieGenres
            }
        }
    }

    /**
     * Update the movie list used on the [RecyclerView].
     */
    private fun updateMovieList(movies: List<Movie>) {
        Log.d(tag, "updateMovieList, list.size = ${movies.size}, movieList.size = ${movieList.value?.size}")
        var updatedList: ArrayList<Movie> = ArrayList()
        if (movieList.value == null) {
            updatedList = ArrayList(movies)
        } else {
            movieList.value?.let {
                if (it.isNotEmpty()) { updatedList = it }
                updatedList.addAll(movies.toSet())
            }
        }
        movieList.postValue(updatedList)
        Log.d(tag, "updateMovieList, movieList.size = ${movieList.value?.size}")
    }

    /**
     * Handles the [ResponseResult] get from the api for a list of movies.
     */
    private fun handleMovieListResult(result: ResponseResult<List<Movie>>) {
        when (result) {
            is ResponseResult.Success -> {
                val resultData = result.data
                Log.d(tag, "Data retrieved with success, size = ${resultData.size}")
                onSuccessfulDataResult(resultData)
            }
            is ResponseResult.Error -> {
                Log.d(tag, "Error loading data: ${result.exception.message}")
                onErrorDataResult()
            }
        }
    }

    /**
     * Handle in case of the [ResponseResult.Success] get from the api for a list of movies.
     */
    private fun onSuccessfulDataResult(list: List<Movie>) {
        Log.d(tag, "onSuccessfulDataResult, list = $list")
        if (list.isNotEmpty()) {
            convertIntGenresToString(list)
            updateFavoriteStatus(list)
            updateMovieList(list)
        }
    }

    /**
     * Handle in case of the [ResponseResult.Error] get from the api.
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

    /**
     * Shows a [Toast] to indicates the user that search will be done only in favorite movies.
     */
    private fun showSearchInFavoriteOnlyToast() {
        weakContext.get()?.let {
            toastMessage.postValue(it.resources.getString(R.string.info_message_search_only_favorite_movies))
            showToastMessage.postValue(true)
        }
    }

    /**
     * Operations available selected to the user.
     */
    enum class OperationSelected {
        NONE,
        POPULAR,
        FAVORITE,
        SEARCH
    }
}

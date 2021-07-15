package com.smarinello.themoviedb.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarinello.themoviedb.R
import com.smarinello.themoviedb.databinding.ActivityMainBinding
import com.smarinello.themoviedb.model.Movie
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.view.adapters.InfiniteScrollListener
import com.smarinello.themoviedb.view.adapters.MovieListAdapter
import com.smarinello.themoviedb.viewmodel.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Activity used to summarize the movies.
 */
@Suppress("TooManyFunctions")
class MainActivity : AppCompatActivity(), MovieListAdapter.MovieListAdapterListener {
    private val tag = this::class.java.simpleName
    private val viewModel: MainActivityViewModel by viewModel()
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    private val movieListAdapter: MovieListAdapter by lazy {
        MovieListAdapter(this)
    }
    private val infiniteScrollListener: InfiniteScrollListener by lazy {
        object : InfiniteScrollListener(linearLayoutManager) {
            override fun loadMore() {
                viewModel.loadAnotherPage()
            }
        }
    }
    private var wentToDetails = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel()
        setupViewModel()
        viewModel.onInitialize()
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume, wentToDetails = $wentToDetails")
        if (wentToDetails) {
            viewModel.onBackFromDetails()
        }
        wentToDetails = false
    }

    /**
     * When [movie] is clicked to get the details.
     */
    override fun onClick(movie: Movie) {
        Log.d(tag, "onClick, movie.title= ${movie.title}")
        wentToDetails = true
        startActivity(
            Intent(this, DetailActivity::class.java).apply {
                putExtra(MovieDetails.EXTRA_MOVIE_ID, movie.id)
            }
        )
    }

    /**
     * When [movie] is clicked to add/remove to the favorite list.
     */
    override fun onClickFavoriteButton(movie: Movie) {
        Log.d(tag, "onClickFavoriteButton, movie.title= ${movie.title}")
        viewModel.onClickFavoriteButton(movie)
    }

    /**
     * Bind view model to this view.
     */
    private fun bindViewModel() {
        Log.d(tag, "bindViewModel")
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchSubmit(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        binding.movieList.apply {
            layoutManager = linearLayoutManager
            adapter = movieListAdapter
            addOnScrollListener(infiniteScrollListener)
        }
    }

    /**
     * Setup view model observers.
     */
    private fun setupViewModel() {
        Log.d(tag, "setupViewModel")
        viewModel.showToastMessage.observe(
            this@MainActivity,
            {
                val toastMessage = viewModel.toastMessage.value.orEmpty()
                if (it && toastMessage.isNotEmpty()) { showToast(toastMessage) }
            }
        )

        viewModel.movieList.observe(
            this@MainActivity,
            {
                if (it.isNotEmpty()) {
                    movieListAdapter.updateData(it)
                } else {
                    movieListAdapter.updateData(listOf())
                }
            }
        )
    }

    /**
     * Shows a [Toast] with [message]
     */
    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Interface to listener the scroll.
     */
    interface ScrollListener {
        /**
         * Action to load another page.
         */
        fun loadAnotherPage()
    }

    /**
     * Interface to listener the favorite button event.
     */
    interface FavoriteButton {
        /**
         * Favorite button was clicked in [movie]
         */
        fun onClickFavoriteButton(movie: Movie)
    }

    /**
     * Interface to listener the Details iteration.
     */
    interface DetailsInterface {
        /**
         * Activity comes back from Details screen.
         */
        fun onBackFromDetails()
    }
}

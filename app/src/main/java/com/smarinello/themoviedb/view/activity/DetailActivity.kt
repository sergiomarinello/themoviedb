package com.smarinello.themoviedb.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarinello.themoviedb.R
import com.smarinello.themoviedb.databinding.ActivityDetailBinding
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.viewmodel.DetailActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Activity used to details the movie.
 */
class DetailActivity : AppCompatActivity() {
    private val tag = this::class.java.simpleName
    private val viewModel: DetailActivityViewModel by viewModel()
    private var binding: ActivityDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel()
        setupViewModel()
        setupToolbar()
        viewModel.start(intent.getIntExtra(MovieDetails.EXTRA_MOVIE_ID, MovieDetails.INVALID_MOVIE_ID))
    }

    /**
     * Bind view model to this view.
     */
    private fun bindViewModel() {
        Log.d(tag, "bindViewModel")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding?.lifecycleOwner = this
        binding?.viewModel = viewModel
    }

    /**
     * Setup view model observers.
     */
    private fun setupViewModel() {
        viewModel.showToastMessage.observe(
            this@DetailActivity,
            {
                val toastMessage = viewModel.toastMessage.value.orEmpty()
                if (it && toastMessage.isNotEmpty()) { showToast(toastMessage) }
            }
        )
    }

    /**
     * Shows a [Toast] with [message]
     */
    private fun showToast(message: String) {
        Toast.makeText(this@DetailActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun setupToolbar() {
        binding?.detailToolbar?.setNavigationOnClickListener { finish() }
    }
}

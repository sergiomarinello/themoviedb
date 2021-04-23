package com.smarinello.themoviedb.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smarinello.themoviedb.BR
import com.smarinello.themoviedb.ImageDetailBinding.loadImage
import com.smarinello.themoviedb.databinding.SummaryMovieBinding
import com.smarinello.themoviedb.extensions.buildPosterUrl
import com.smarinello.themoviedb.model.Movie
import kotlin.properties.Delegates

/**
 * [RecyclerView.Adapter] to add [Movie] data to the [RecyclerView] list.
 */
class MovieListAdapter(
    private val listener: MovieListAdapterListener
) : RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {

    private var movieList: List<Movie> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        val binding = SummaryMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieListViewHolder(binding)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {

        if (position != RecyclerView.NO_POSITION) {
            val movie: Movie = movieList[position]
            holder.updateData(movie)
            holder.bind(movie)
        }
    }

    fun updateData(movies: List<Movie>) {
        movieList = movies
    }

    inner class MovieListViewHolder(
        private val binding: SummaryMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Updates the movie data to summary.
         */
        fun updateData(movie: Movie) {
            binding.setVariable(BR.summary, movie)
        }

        /**
         * Binds [movie] to what could not be bind in [updateData].
         */
        fun bind(movie: Movie) {
            loadImage(binding.summaryPosterImageView, movie.buildPosterUrl())
            binding.apply {
                itemView.setOnClickListener { listener.onClick(movie) }
                favoriteFab.setOnClickListener { listener.onClickFavoriteButton(movie) }
            }
        }
    }

    /**
     * Selected movie item interface.
     */
    interface MovieListAdapterListener {
        /**
         * This method will inform the [movie] was clicked.
         */
        fun onClick(movie: Movie)

        /**
         * This method will inform that the favorite button of [movie] was clicked.
         */
        fun onClickFavoriteButton(movie: Movie)
    }
}

package com.smarinello.themoviedb.robot

import com.smarinello.themoviedb.R

fun movieList(func: MovieListRobot.() -> Unit) = MovieListRobot().apply { func() }

/**
 * Robot class related to movie list shown in "Popular", "Search" and "Favorite".
 */
class MovieListRobot : BaseTestRobot() {

    fun clickFavoriteFab(position: Int = 0) {
        clickOnItemOfRecyclerViewAtPosition(R.id.movie_list, R.id.favorite_fab, position)
    }

    fun clickSummaryPosterImage(position: Int = 0) {
        clickOnItemOfRecyclerViewAtPosition(R.id.movie_list, R.id.summary_poster_image_view, position)
    }
}

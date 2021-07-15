package com.smarinello.themoviedb.robot

import com.smarinello.themoviedb.R

fun detailsMovie(func: DetailsMovieRobot.() -> Unit) = DetailsMovieRobot().apply { func() }

/**
 * Robot class related to movie's details.
 */
class DetailsMovieRobot : BaseTestRobot() {

    fun checkOverviewText(text: String) {
        matchText(R.id.detail_overview, text)
    }
}

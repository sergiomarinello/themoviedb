package com.smarinello.themoviedb.robot

import com.smarinello.themoviedb.R

fun popular(func: PopularRobot.() -> Unit) = PopularRobot().apply { func() }

/**
 * Robot class related to "Popular".
 */
class PopularRobot : BaseTestRobot() {

    fun matchTextColor(resColorId: Int) {
        matchTextColor(R.id.chip_popular, resColorId)
    }

    fun performClick() {
        performClick(R.id.chip_popular)
    }
}

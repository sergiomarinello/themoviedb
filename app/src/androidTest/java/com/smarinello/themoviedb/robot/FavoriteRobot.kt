package com.smarinello.themoviedb.robot

import com.smarinello.themoviedb.R

fun favorite(func: FavoriteRobot.() -> Unit) = FavoriteRobot().apply { func() }

/**
 * Robot class related to "Favorite".
 */
class FavoriteRobot : BaseTestRobot() {

    fun matchTextColor(resColorId: Int) {
        matchTextColor(R.id.chip_favorite, resColorId)
    }

    fun performClick() {
        performClick(R.id.chip_favorite)
    }
}

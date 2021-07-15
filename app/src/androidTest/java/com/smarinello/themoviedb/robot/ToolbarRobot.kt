package com.smarinello.themoviedb.robot

import com.smarinello.themoviedb.R

fun toolbar(func: ToolbarRobot.() -> Unit) = ToolbarRobot().apply { func() }

/**
 * Robot class related to the toolbar in the activity related to the details of the movie.
 */
class ToolbarRobot : BaseTestRobot() {

    fun hasDescendantWithText(text: String) {
        hasDescendantWithText(R.id.detail_toolbar, text)
    }
}

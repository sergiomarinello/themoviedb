package com.smarinello.themoviedb.robot

import com.smarinello.themoviedb.R

fun search(func: SearchRobot.() -> Unit) = SearchRobot().apply { func() }

/**
 * Robot class related to "Search".
 */
class SearchRobot : BaseTestRobot() {

    fun enterText(text: String) {
        enterText(R.id.search_src_text, text)
    }

    fun matchText(text: String) {
        matchText(R.id.search_src_text, text)
    }

    fun replaceText(text: String) {
        replaceText(R.id.search_src_text, text)
    }
}

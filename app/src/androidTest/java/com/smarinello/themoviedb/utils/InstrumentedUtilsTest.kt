package com.smarinello.themoviedb.utils

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import org.hamcrest.Matcher

object InstrumentedUtilsTest {

    fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints(): Matcher<View>? = null

        override fun getDescription() = "Click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) =
            click().perform(uiController, view.findViewById<View>(viewId))
    }

    /**
     * Search needs to be delayed to get the values from the internet.
     */
    fun delayToGetResults() {
        Espresso.onView(isRoot()).perform(waitFor(2000))
    }

    private fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}

package com.smarinello.themoviedb.robot

import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

/**
 * Base test robot class.
 * Encapsulate the access to the view elements.
 */
open class BaseTestRobot {

    fun performClick(resId: Int): ViewInteraction =
        Espresso.onView((ViewMatchers.withId(resId))).perform(ViewActions.click())

    fun matchTextColor(resId: Int, resColorId: Int): ViewInteraction =
        Espresso.onView(ViewMatchers.withId(resId))
            .check(ViewAssertions.matches(ViewMatchers.hasTextColor(resColorId)))

    fun clickOnItemOfRecyclerViewAtPosition(
        recyclerViewId: Int,
        itemInRecyclerViewId: Int,
        position: Int = 0
    ): ViewInteraction =
        Espresso.onView(ViewMatchers.withId(recyclerViewId)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position, clickOnViewChild(itemInRecyclerViewId)
            )
        )

    fun enterText(resId: Int, text: String): ViewInteraction =
        Espresso.onView(ViewMatchers.withId(resId))
            .perform(ViewActions.typeText(text), ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))

    fun matchText(resId: Int, text: String): ViewInteraction =
        Espresso.onView(ViewMatchers.withId(resId))
            .check(ViewAssertions.matches(ViewMatchers.withText(text)))

    fun replaceText(resId: Int, text: String): ViewInteraction =
        Espresso.onView(ViewMatchers.withId(resId)).perform(ViewActions.replaceText(text))

    fun waitFor(delayTime: Long) =
        Espresso.onView(ViewMatchers.isRoot()).perform(rootWaitFor(delayTime))

    fun hasDescendantWithText(resId: Int, text: String): ViewInteraction =
        Espresso.onView(ViewMatchers.withId(resId)).check(
            ViewAssertions.matches(
                ViewMatchers.hasDescendant(ViewMatchers.withText(text))
            )
        )

    private fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints(): Matcher<View>? = null
        override fun getDescription() = "Click on a child view with specified id."
        override fun perform(uiController: UiController, view: View) =
            ViewActions.click().perform(uiController, view.findViewById<View>(viewId))
    }

    private fun rootWaitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}

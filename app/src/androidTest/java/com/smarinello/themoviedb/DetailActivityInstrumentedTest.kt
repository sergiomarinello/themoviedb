package com.smarinello.themoviedb

import android.content.Intent
import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.smarinello.themoviedb.model.MovieDetails
import com.smarinello.themoviedb.utils.ConnectivityUtils
import com.smarinello.themoviedb.view.activity.DetailActivity
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * This class consist to verify the DetailActivity and demands internet connection.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class DetailActivityInstrumentedTest {

    /**
     * Valid movie id: John Wick
     */
    private val validMovieId: Int = 245891
    private val validMovieTitle: String = "John Wick"
    private val validMovieOverview: String =
        "Ex-hitman John Wick comes out of retirement to track down the gangsters that took everything from him."

    private val intent = Intent(
        ApplicationProvider.getApplicationContext(),
        DetailActivity::class.java
    ).putExtra(MovieDetails.EXTRA_MOVIE_ID, validMovieId)

    @get:Rule
    val activityRule = ActivityScenarioRule<DetailActivity>(intent)

    @Before
    fun setup() {
        // Set max timeout to execute each test otherwise it fails.
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)

        // Check internet connection
        activityRule.scenario.onActivity { activity ->
            Assert.assertEquals(ConnectivityUtils(activity).isInternetAccessAvailable(), true)
        }
    }

    @Test
    fun checkMovieTitle() {
        delayToGetResults()
        Espresso.onView(ViewMatchers.withId(R.id.detail_toolbar)).check(
            ViewAssertions.matches(
                ViewMatchers.hasDescendant(ViewMatchers.withText(validMovieTitle))
            )
        )
    }

    @Test
    fun checkMovieOverview() {
        delayToGetResults()
        Espresso.onView(ViewMatchers.withId(R.id.detail_overview)).check(
            ViewAssertions.matches(ViewMatchers.withText(validMovieOverview))
        )
    }

    @After
    fun cleanup() {
        val scenario = activityRule.scenario
        scenario.close()
    }

    //region auxiliary methods
    /**
     * Get if the Loading progress bar is showing.
     */
    private fun isLoadingProgressBarVisible(): Boolean {
        var contentLoadingProgressBar: ContentLoadingProgressBar? = null
        activityRule.scenario.onActivity {
            contentLoadingProgressBar = it.findViewById(R.id.detail_spinner_loading)
        }
        return contentLoadingProgressBar?.isVisible ?: false
    }

    /**
     * Database access needs to be delayed to get the values from the internet.
     * The best way to avoid a hardcoded delay is checking the progress bar visibility and as side
     * effect is blocking the test.
     */
    private fun delayToGetResults() {
        while (isLoadingProgressBarVisible()) {
            // Do nothing. It is getting the information from the server.
        }
    }
    //endregion
}

package com.smarinello.themoviedb

import androidx.core.view.isVisible
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.smarinello.themoviedb.robot.BaseTestRobot
import com.smarinello.themoviedb.robot.favorite
import com.smarinello.themoviedb.robot.movieList
import com.smarinello.themoviedb.robot.popular
import com.smarinello.themoviedb.robot.search
import com.smarinello.themoviedb.utils.ConnectivityUtils
import com.smarinello.themoviedb.view.activity.DetailActivity
import com.smarinello.themoviedb.view.activity.MainActivity
import org.hamcrest.Matchers.greaterThan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

private const val INVALID_COUNT_ITEMS_VALUE: Int = -1

/**
 * This class consist to verify the MainActivity basic functions and demands internet connection
 * and a valid API key.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityInstrumentedTest {
    private val invalidApiKeyValue: String = "put the key value here"

    private val invalidSearchMovieTitle: String = "**********************----------++++++++++++++++"
    private val invalidNumberItemsMessage: String = "invalid number of items"
    /**
     * In 2021, there is at least 3 John Wick's movies
     */
    private val validSearchMovieTitle: String = "John Wick"
    private val minMoviesWithTitle: Int = 2

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        // Set max timeout to execute each test otherwise it fails.
        IdlingPolicies.setMasterPolicyTimeout(1, TimeUnit.MINUTES)
        IdlingPolicies.setIdlingResourceTimeout(1, TimeUnit.MINUTES)

        // Check for a valid API key
        assertNotEquals(BuildConfig.API_KEY_VALUE, invalidApiKeyValue)

        // Check internet connection.
        activityRule.scenario.onActivity { activity ->
            assertEquals(ConnectivityUtils(activity).isInternetAccessAvailable(), true)
        }
    }

    //region Search
    /**
     * Search with a valid movie title and checks if it returns a minimum size.
     */
    @Test
    fun validSearchTest() {
        search {
            enterText(validSearchMovieTitle)
            matchText(validSearchMovieTitle)
        }
        delayToGetResults()
        assertThat(invalidNumberItemsMessage, movieListCountItem(), greaterThan(minMoviesWithTitle))
    }

    /**
     * Search with an invalid movie title.
     */
    @Test
    fun invalidSearchTest() {
        search {
            enterText(invalidSearchMovieTitle)
            matchText(invalidSearchMovieTitle)
        }
        // Verify if there is at least the minimum title expected.
        delayToGetResults()
        assertEquals(movieListCountItem(), 0)
    }

    /**
     * Test if a search removes the chip selection correctly in "Popular".
     */
    @Test
    fun searchRemovePopularSelectionTest() {
        clickInPopularChip()
        validSearchTest()
        popular {
            matchTextColor(R.color.chips_text_color_not_checked)
        }
    }

    /**
     * Test if a search removes the chip selection correctly in "Favorite".
     */
    @Test
    fun searchRemoveFavoriteSelectionTest() {
        clickInFavoriteChip()
        validSearchTest()
        favorite {
            matchTextColor(R.color.chips_text_color_not_checked)
        }
    }

    /**
     * Add and remove a favorite movie from "Search".
     */
    @Test
    fun addAndRemoveFavoriteMovieFromSearchChipTest() {
        // Clean favorite to have a test control.
        cleanFavoriteFabTest()
        validSearchTest()
        clickInFirstFavoriteFabElement()
        clickInFavoriteChip()
        assertEquals(movieListCountItem(), 1)

        // Remove text that is in the Search.
        search {
            replaceText("")
        }
        validSearchTest()
        clickInFirstFavoriteFabElement()
        clickInFavoriteChip()
        assertEquals(movieListCountItem(), 0)
    }

    /**
     * Verify if a click in the first item of the movie list obtained by a search sends it to the
     * detailed screen.
     */
    @Test
    fun clickInMovieTakesToDetailsFromSearchResultTest() {
        validSearchTest()
        Intents.init()
        clickInFirstItemInTheMovieList()
        delayToGetResults()
        intended(hasComponent(DetailActivity::class.java.name))
        Intents.release()
    }
    //endregion

    //region Popular
    /**
     * Check if the list is populated in the "Popular".
     */
    @Test
    fun getPopularMoviesTest() {
        clickInPopularChip()
        val itemCount: Int = movieListCountItem()
        assertThat(invalidNumberItemsMessage, itemCount, greaterThan(0))
    }

    /**
     * Verify if a click in the first item of the "Popular" movie list sends it to the
     * detailed screen.
     */
    @Test
    fun clickInMovieTakesToDetailsFromPopularTest() {
        getPopularMoviesTest()
        Intents.init()
        clickInFirstItemInTheMovieList()
        delayToGetResults()
        intended(hasComponent(DetailActivity::class.java.name))
        Intents.release()
    }

    /**
     * Add and remove a favorite movie from "Popular".
     */
    @Test
    fun addAndRemoveFavoriteMovieFromPopularChipTest() {
        clickInPopularChip()
        // Clean favorite to have a test control.
        cleanFavoriteFabTest()
        // Add the first movie to be favorite.
        clickInPopularChip()
        clickInFirstFavoriteFabElement()
        clickInFavoriteChip()
        assertEquals(movieListCountItem(), 1)

        // Remove the movie in favorite.
        clickInPopularChip()
        clickInFirstFavoriteFabElement()
        clickInFavoriteChip()
        assertEquals(movieListCountItem(), 0)
    }
    //endregion

    //region Favorite
    /**
     * Click in "Favorite" chip is selected correctly.
     */
    @Test
    fun clickInFavoriteMovieTest() {
        popular {
            performClick()
        }
        favorite {
            matchTextColor(R.color.chips_text_color_not_checked)
            performClick()
            matchTextColor(R.color.chips_text_color_checked)
        }
        delayToGetResults()
    }

    /**
     * Verify if a click in the first item of the "Favorite" movie list sends it to the
     * detailed screen.
     */
    @Test
    fun clickInMovieTakesToDetailsFromFavoriteTest() {
        // Clean favorite to have a test control.
        cleanFavoriteFabTest()
        // Add the first movie to be favorite.
        clickInPopularChip()
        clickInFirstFavoriteFabElement()

        clickInFavoriteChip()
        Intents.init()
        clickInFirstItemInTheMovieList()
        delayToGetResults()
        intended(hasComponent(DetailActivity::class.java.name))
        Intents.release()
    }

    /**
     * Remove an entry in "Favorite".
     */
    @Test
    fun removeFavoriteMovieFromFavoritesTest() {
        // Guarantee that is at least one movie in the "Favorite".
        cleanFavoriteFabTest()
        clickInPopularChip()
        clickInFirstFavoriteFabElement()
        clickInFavoriteChip()
        assertEquals(movieListCountItem(), 1)
        // Remove the movie from "Favorite".
        clickInFirstFavoriteFabElement()
        assertEquals(movieListCountItem(), 0)
    }
    //endregion

    @Before
    fun tearDown() {
        val baseTestRobot: BaseTestRobot = BaseTestRobot()
        baseTestRobot.waitFor(2000)
    }

    //region auxiliary methods
    /**
     * Remove any entry in "Favorite".
     */
    private fun cleanFavoriteFabTest() {
        clickInFavoriteChip()

        val initialMovieListSize = movieListCountItem()

        movieList {
            for (i in 1..initialMovieListSize) {
                clickFavoriteFab()
                assertEquals(initialMovieListSize - i, movieListCountItem())
            }
        }
        assertEquals(movieListCountItem(), 0)
    }

    /**
     * Do a click in Favorite FAB.
     */
    private fun clickInFirstFavoriteFabElement() {
        movieList {
            clickFavoriteFab(0)
        }
    }

    /**
     * Do a click in the first movie item shown in the movie list.
     */
    private fun clickInFirstItemInTheMovieList() {
        movieList {
            clickSummaryPosterImage(0)
        }
    }

    /**
     * Click in "Popular" chip.
     */
    private fun clickInPopularChip() {
        popular {
            performClick()
            matchTextColor(R.color.chips_text_color_checked)
        }
        delayToGetResults()
    }

    /**
     * Click in "Favorite" chip.
     */
    private fun clickInFavoriteChip() {
        favorite {
            performClick()
            matchTextColor(R.color.chips_text_color_checked)
        }
        delayToGetResults()
    }

    /**
     * Get the number of the items in the movie list.
     */
    private fun movieListCountItem(): Int {
        var recyclerView: RecyclerView? = null
        activityRule.scenario.onActivity {
            recyclerView = it.findViewById(R.id.movie_list)
        }
        return recyclerView?.adapter?.itemCount ?: INVALID_COUNT_ITEMS_VALUE
    }

    /**
     * Get if the Loading progress bar is showing.
     */
    private fun isLoadingProgressBarVisible(): Boolean {
        var contentLoadingProgressBar: ContentLoadingProgressBar? = null
        activityRule.scenario.onActivity {
            contentLoadingProgressBar = it.findViewById(R.id.spinnerLoading)
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

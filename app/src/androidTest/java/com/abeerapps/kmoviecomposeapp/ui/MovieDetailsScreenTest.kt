package com.abeerapps.kmoviecomposeapp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.abeerapps.kmoviecomposeapp.R
import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.DataError
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails
import com.abeerapps.kmoviecomposeapp.domain.usecase.MovieWithCast
import com.abeerapps.kmoviecomposeapp.ui.common.ContentState
import com.abeerapps.kmoviecomposeapp.ui.details.MovieDetailsScreen
import com.abeerapps.kmoviecomposeapp.ui.theme.KMovieComposeAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDetailsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun setScreen(state: ContentState<MovieWithCast>, onBack: () -> Unit = {}, onRetry: () -> Unit = {}) =
        composeRule.setContent {
            KMovieComposeAppTheme { MovieDetailsScreen(uiState = state, onBack = onBack, onRetry = onRetry) }
        }

    @Test
    fun showsDetailsAndCast() {
        setScreen(
            ContentState.Success(
                MovieWithCast(
                    MovieDetails(1, "Arrival", "Linguist meets aliens.", null, 7.9, null, 116, emptyList()),
                    listOf(CastMember(1, "Amy Adams", "Louise", null))
                )
            )
        )

        composeRule.onNodeWithText("Linguist meets aliens.").assertIsDisplayed()
        composeRule.onNodeWithText("Amy Adams").assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.runtime_minutes, 116)).assertIsDisplayed()
    }

    @Test
    fun errorShowsMessageAndRetryCallsBack() {
        var retries = 0
        setScreen(ContentState.Error(DataError.Network()), onRetry = { retries++ })

        composeRule.onNodeWithText(context.getString(R.string.error_network)).assertIsDisplayed()
        composeRule.onNodeWithText(context.getString(R.string.retry)).performClick()

        assertEquals(1, retries)
    }

    @Test
    fun backButtonCallsBack() {
        var backs = 0
        setScreen(ContentState.Loading, onBack = { backs++ })

        composeRule.onNodeWithContentDescription(context.getString(R.string.back)).performClick()

        assertEquals(1, backs)
    }
}

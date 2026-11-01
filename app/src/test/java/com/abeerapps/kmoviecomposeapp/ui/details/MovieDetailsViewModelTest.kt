package com.abeerapps.kmoviecomposeapp.ui.details

import androidx.lifecycle.SavedStateHandle
import com.abeerapps.kmoviecomposeapp.domain.model.DataError
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetMovieDetailsUseCase
import com.abeerapps.kmoviecomposeapp.testutil.FakeMovieRepository
import com.abeerapps.kmoviecomposeapp.testutil.MainDispatcherRule
import com.abeerapps.kmoviecomposeapp.ui.common.ContentState
import com.abeerapps.kmoviecomposeapp.ui.navigation.MovieDetailsDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MovieDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeMovieRepository()

    private fun createViewModel(movieId: Long = 42) = MovieDetailsViewModel(
        SavedStateHandle(mapOf(MovieDetailsDestination.ARG_MOVIE_ID to movieId)),
        GetMovieDetailsUseCase(repository)
    )

    @Test
    fun `loads the movie from the navigation argument`() {
        val state = createViewModel(movieId = 7).uiState.value as ContentState.Success

        assertEquals(7L, state.data.details.id)
        assertEquals("Actor", state.data.cast.single().name)
    }

    @Test
    fun `details failure shows error and retry reloads`() {
        repository.details = Result.failure(DataError.Network())
        val viewModel = createViewModel()

        assertTrue((viewModel.uiState.value as ContentState.Error).error is DataError.Network)

        repository.details = FakeMovieRepository().details
        viewModel.retry()

        assertTrue(viewModel.uiState.value is ContentState.Success)
        assertEquals(2, repository.detailsRequests)
    }
}

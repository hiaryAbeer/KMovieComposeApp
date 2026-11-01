package com.abeerapps.kmoviecomposeapp.ui.movies

import androidx.paging.testing.asSnapshot
import com.abeerapps.kmoviecomposeapp.domain.model.DataError
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetGenresUseCase
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetMoviesByGenreUseCase
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetTrendingMoviesUseCase
import com.abeerapps.kmoviecomposeapp.testutil.FakeMovieRepository
import com.abeerapps.kmoviecomposeapp.testutil.MainDispatcherRule
import com.abeerapps.kmoviecomposeapp.testutil.testMovie
import com.abeerapps.kmoviecomposeapp.ui.common.ContentState
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MoviesViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeMovieRepository()

    // `genreMovies` never completes (it is cached in viewModelScope), so snapshot only its current value.
    private suspend fun MoviesViewModel.currentGenreMovies() = genreMovies.take(1).asSnapshot().map { it.id }

    private fun createViewModel() = MoviesViewModel(
        GetTrendingMoviesUseCase(repository),
        GetGenresUseCase(repository),
        GetMoviesByGenreUseCase(repository)
    )

    @Test
    fun `loads trending and genres and selects the first genre`() {
        val state = createViewModel().uiState.value

        assertEquals(ContentState.Success(listOf(testMovie(1), testMovie(2))), state.trending)
        assertTrue(state.genres is ContentState.Success)
        assertEquals(28, state.selectedGenreId)
    }

    @Test
    fun `trending failure shows error and retry recovers`() {
        repository.trending = Result.failure(DataError.Network())
        val viewModel = createViewModel()

        val error = viewModel.uiState.value.trending as ContentState.Error
        assertTrue(error.error is DataError.Network)

        repository.trending = Result.success(listOf(testMovie(9)))
        viewModel.retryTrending()

        assertEquals(ContentState.Success(listOf(testMovie(9))), viewModel.uiState.value.trending)
    }

    @Test
    fun `genre failure leaves no genre selected until retry succeeds`() {
        repository.genres = Result.failure(DataError.Unauthorized())
        val viewModel = createViewModel()

        assertTrue(viewModel.uiState.value.genres is ContentState.Error)
        assertEquals(null, viewModel.uiState.value.selectedGenreId)

        repository.genres = Result.success(listOf(Genre(12, "Adventure")))
        viewModel.retryGenres()

        assertEquals(12, viewModel.uiState.value.selectedGenreId)
    }

    @Test
    fun `genre movies follow the latest selected genre`() = runTest {
        val viewModel = createViewModel()

        assertEquals(listOf(2800L, 2801L, 2802L), viewModel.currentGenreMovies())

        viewModel.onGenreSelected(35)

        assertEquals(listOf(3500L, 3501L, 3502L), viewModel.currentGenreMovies())
        assertEquals(listOf(28, 35), repository.requestedGenres)
    }

    @Test
    fun `selecting the same genre again does not reload it`() = runTest {
        val viewModel = createViewModel()
        viewModel.currentGenreMovies()

        viewModel.onGenreSelected(28)
        viewModel.currentGenreMovies()

        assertEquals(listOf(28), repository.requestedGenres)
    }
}

package com.abeerapps.kmoviecomposeapp.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetGenresUseCase
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetMoviesByGenreUseCase
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetTrendingMoviesUseCase
import com.abeerapps.kmoviecomposeapp.ui.common.ContentState
import com.abeerapps.kmoviecomposeapp.ui.common.toContentState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MoviesUiState(
    val trending: ContentState<List<Movie>> = ContentState.Loading,
    val genres: ContentState<List<Genre>> = ContentState.Loading,
    val selectedGenreId: Int? = null
)

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getTrendingMovies: GetTrendingMoviesUseCase,
    private val getGenres: GetGenresUseCase,
    getMoviesByGenre: GetMoviesByGenreUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    /**
     * Movies for the selected genre. `flatMapLatest` cancels the previous genre's
     * loading when the selection changes, so a slow response can never replace
     * the newer genre's movies.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val genreMovies: Flow<PagingData<Movie>> =
        _uiState
            .map { it.selectedGenreId }
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { getMoviesByGenre(it) }
            .cachedIn(viewModelScope)

    init {
        loadTrending()
        loadGenres()
    }

    fun onGenreSelected(genreId: Int) {
        _uiState.update { it.copy(selectedGenreId = genreId) }
    }

    fun retryTrending() = loadTrending()

    fun retryGenres() = loadGenres()

    private fun loadTrending() {
        _uiState.update { it.copy(trending = ContentState.Loading) }
        viewModelScope.launch {
            val trending = getTrendingMovies().toContentState()
            _uiState.update { it.copy(trending = trending) }
        }
    }

    private fun loadGenres() {
        _uiState.update { it.copy(genres = ContentState.Loading) }
        viewModelScope.launch {
            val result = getGenres()
            _uiState.update { state ->
                state.copy(
                    genres = result.toContentState(),
                    selectedGenreId = state.selectedGenreId ?: result.getOrNull()?.firstOrNull()?.id
                )
            }
        }
    }
}

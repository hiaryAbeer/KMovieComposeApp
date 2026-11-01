package com.abeerapps.kmoviecomposeapp.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetMovieDetailsUseCase
import com.abeerapps.kmoviecomposeapp.domain.usecase.MovieWithCast
import com.abeerapps.kmoviecomposeapp.ui.common.ContentState
import com.abeerapps.kmoviecomposeapp.ui.common.toContentState
import com.abeerapps.kmoviecomposeapp.ui.navigation.MovieDetailsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetails: GetMovieDetailsUseCase
) : ViewModel() {
    // The id comes from the navigation arguments, which survive process death.
    private val movieId: Long = checkNotNull(savedStateHandle[MovieDetailsDestination.ARG_MOVIE_ID])

    private val _uiState = MutableStateFlow<ContentState<MovieWithCast>>(ContentState.Loading)
    val uiState: StateFlow<ContentState<MovieWithCast>> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        _uiState.value = ContentState.Loading
        viewModelScope.launch {
            _uiState.value = getMovieDetails(movieId).toContentState()
        }
    }
}

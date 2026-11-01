package com.abeerapps.kmoviecomposeapp.ui.movies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.abeerapps.kmoviecomposeapp.R
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.ui.common.ContentState
import com.abeerapps.kmoviecomposeapp.ui.common.ErrorMessage
import com.abeerapps.kmoviecomposeapp.ui.common.LoadingIndicator
import com.abeerapps.kmoviecomposeapp.ui.common.MoviePoster
import com.abeerapps.kmoviecomposeapp.ui.theme.KMovieComposeAppTheme
import kotlinx.coroutines.flow.flowOf

/** Connects [MoviesScreen] to its ViewModel. */
@Composable
fun MoviesRoute(onMovieClick: (movieId: Long) -> Unit, viewModel: MoviesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MoviesScreen(
        uiState = uiState,
        genreMovies = viewModel.genreMovies.collectAsLazyPagingItems(),
        onGenreSelected = viewModel::onGenreSelected,
        onMovieClick = onMovieClick,
        onRetryTrending = viewModel::retryTrending,
        onRetryGenres = viewModel::retryGenres
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    uiState: MoviesUiState,
    genreMovies: LazyPagingItems<Movie>,
    onGenreSelected: (Int) -> Unit,
    onMovieClick: (Long) -> Unit,
    onRetryTrending: () -> Unit,
    onRetryGenres: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 110.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag(MoviesScreenTags.GRID)
        ) {
            fullWidthItem { SectionTitle(stringResource(R.string.trending)) }
            fullWidthItem {
                TrendingSection(uiState.trending, onMovieClick = onMovieClick, onRetry = onRetryTrending)
            }
            fullWidthItem { SectionTitle(stringResource(R.string.browse_by_genre)) }
            fullWidthItem {
                GenreTabs(uiState.genres, uiState.selectedGenreId, onGenreSelected, onRetryGenres)
            }
            if (uiState.genres is ContentState.Success) {
                genreMoviesItems(genreMovies, onMovieClick)
            }
        }
    }
}

object MoviesScreenTags {
    const val GRID = "movies_grid"
}

private fun LazyGridScope.fullWidthItem(content: @Composable () -> Unit) {
    item(span = { GridItemSpan(maxLineSpan) }) { content() }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
private fun TrendingSection(state: ContentState<List<Movie>>, onMovieClick: (Long) -> Unit, onRetry: () -> Unit) {
    when (state) {
        ContentState.Loading -> LoadingIndicator()
        is ContentState.Error -> ErrorMessage(state.error, onRetry)
        is ContentState.Success ->
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.data, key = { it.id }) { movie ->
                    MoviePoster(
                        posterPath = movie.posterPath,
                        contentDescription = movie.title,
                        modifier = Modifier.width(140.dp).clickable { onMovieClick(movie.id) }
                    )
                }
            }
    }
}

@Composable
private fun GenreTabs(
    state: ContentState<List<Genre>>,
    selectedGenreId: Int?,
    onGenreSelected: (Int) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        ContentState.Loading -> LoadingIndicator()
        is ContentState.Error -> ErrorMessage(state.error, onRetry)
        is ContentState.Success -> {
            val genres = state.data
            if (genres.isEmpty()) return
            val selectedIndex = genres.indexOfFirst { it.id == selectedGenreId }.coerceAtLeast(0)
            PrimaryScrollableTabRow(selectedTabIndex = selectedIndex, edgePadding = 0.dp) {
                genres.forEachIndexed { index, genre ->
                    Tab(
                        selected = index == selectedIndex,
                        onClick = { onGenreSelected(genre.id) },
                        text = { Text(genre.name) }
                    )
                }
            }
        }
    }
}

private fun LazyGridScope.genreMoviesItems(movies: LazyPagingItems<Movie>, onMovieClick: (Long) -> Unit) {
    when (val refresh = movies.loadState.refresh) {
        is LoadState.Loading -> fullWidthItem { LoadingIndicator() }
        is LoadState.Error -> fullWidthItem { ErrorMessage(refresh.error, onRetry = movies::retry) }
        is LoadState.NotLoading -> {
            if (movies.itemCount == 0) {
                fullWidthItem {
                    Text(stringResource(R.string.no_movies), modifier = Modifier.padding(24.dp))
                }
            }
            items(count = movies.itemCount, key = movies.itemKey { it.id }) { index ->
                movies[index]?.let { movie ->
                    MoviePoster(
                        posterPath = movie.posterPath,
                        contentDescription = movie.title,
                        modifier = Modifier.clickable { onMovieClick(movie.id) }
                    )
                }
            }
            when (val append = movies.loadState.append) {
                is LoadState.Loading -> fullWidthItem { LoadingIndicator() }
                is LoadState.Error -> fullWidthItem { ErrorMessage(append.error, onRetry = movies::retry) }
                is LoadState.NotLoading -> Unit
            }
        }
    }
}

private val previewMovies =
    List(6) { Movie(it.toLong(), "Movie $it", "", null, null, 7.5) }

@Preview(showBackground = true)
@Composable
private fun MoviesScreenPreview() {
    KMovieComposeAppTheme {
        MoviesScreen(
            uiState =
            MoviesUiState(
                trending = ContentState.Success(previewMovies),
                genres = ContentState.Success(listOf(Genre(28, "Action"), Genre(35, "Comedy"))),
                selectedGenreId = 28
            ),
            genreMovies = flowOf(PagingData.from(previewMovies)).collectAsLazyPagingItems(),
            onGenreSelected = {},
            onMovieClick = {},
            onRetryTrending = {},
            onRetryGenres = {}
        )
    }
}

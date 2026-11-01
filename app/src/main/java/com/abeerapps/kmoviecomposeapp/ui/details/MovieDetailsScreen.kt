package com.abeerapps.kmoviecomposeapp.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abeerapps.kmoviecomposeapp.R
import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails
import com.abeerapps.kmoviecomposeapp.domain.usecase.MovieWithCast
import com.abeerapps.kmoviecomposeapp.ui.common.ContentState
import com.abeerapps.kmoviecomposeapp.ui.common.ErrorMessage
import com.abeerapps.kmoviecomposeapp.ui.common.LoadingIndicator
import com.abeerapps.kmoviecomposeapp.ui.common.MoviePoster
import com.abeerapps.kmoviecomposeapp.ui.common.ProfileImage
import com.abeerapps.kmoviecomposeapp.ui.theme.KMovieComposeAppTheme

/** Connects [MovieDetailsScreen] to its ViewModel. */
@Composable
fun MovieDetailsRoute(onBack: () -> Unit, viewModel: MovieDetailsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MovieDetailsScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::retry)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    uiState: ContentState<MovieWithCast>,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? ContentState.Success)?.data?.details?.title.orEmpty()
                    Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            when (uiState) {
                ContentState.Loading -> LoadingIndicator()
                is ContentState.Error -> ErrorMessage(uiState.error, onRetry)
                is ContentState.Success -> MovieDetailsContent(uiState.data)
            }
        }
    }
}

@Composable
private fun MovieDetailsContent(movie: MovieWithCast) {
    val details = movie.details
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MoviePoster(
                    posterPath = details.posterPath,
                    contentDescription = details.title,
                    modifier = Modifier.width(140.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(details.title, style = MaterialTheme.typography.headlineSmall)
                    MetaText(stringResource(R.string.rating, details.voteAverage))
                    details.releaseDate?.let { MetaText(it) }
                    details.runtimeMinutes?.let { MetaText(stringResource(R.string.runtime_minutes, it)) }
                    if (details.genres.isNotEmpty()) MetaText(details.genres.joinToString { it.name })
                }
            }
        }
        if (details.overview.isNotBlank()) {
            item {
                SectionTitle(stringResource(R.string.overview))
                Text(details.overview, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (movie.cast.isNotEmpty()) {
            item {
                SectionTitle(stringResource(R.string.cast))
                CastRow(movie.cast)
            }
        }
    }
}

@Composable
private fun MetaText(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun CastRow(cast: List<CastMember>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(cast, key = { it.id }) { member ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
                ProfileImage(member.profilePath, contentDescription = member.name, modifier = Modifier.size(72.dp))
                Spacer(Modifier.height(4.dp))
                Text(
                    member.name,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieDetailsScreenPreview() {
    KMovieComposeAppTheme {
        MovieDetailsScreen(
            uiState =
            ContentState.Success(
                MovieWithCast(
                    details =
                    MovieDetails(
                        id = 1,
                        title = "Preview Movie",
                        overview = "A short overview of the movie.",
                        posterPath = null,
                        voteAverage = 8.2,
                        releaseDate = "2025-01-01",
                        runtimeMinutes = 124,
                        genres = listOf(Genre(28, "Action"))
                    ),
                    cast = List(4) { CastMember(it.toLong(), "Actor $it", null, null) }
                )
            ),
            onBack = {},
            onRetry = {}
        )
    }
}

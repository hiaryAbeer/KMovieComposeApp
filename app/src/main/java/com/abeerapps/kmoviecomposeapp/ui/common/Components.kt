package com.abeerapps.kmoviecomposeapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.abeerapps.kmoviecomposeapp.BuildConfig
import com.abeerapps.kmoviecomposeapp.R

fun tmdbImageUrl(path: String?): String? = path?.let { BuildConfig.TMDB_IMAGE_BASE_URL + it }

/** Rounded 2:3 TMDB poster. */
@Composable
fun MoviePoster(posterPath: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = tmdbImageUrl(posterPath),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier =
        modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

/** Circular TMDB profile photo. */
@Composable
fun ProfileImage(profilePath: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = tmdbImageUrl(profilePath),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier =
        modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(error: Throwable, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(error.messageRes()),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
    }
}

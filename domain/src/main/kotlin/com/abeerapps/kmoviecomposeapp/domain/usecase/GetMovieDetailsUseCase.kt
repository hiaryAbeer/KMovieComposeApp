package com.abeerapps.kmoviecomposeapp.domain.usecase

import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails
import com.abeerapps.kmoviecomposeapp.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/** A movie's details together with its cast. */
data class MovieWithCast(val details: MovieDetails, val cast: List<CastMember>)

/**
 * Loads details and cast in parallel. The cast is optional: if only the cast
 * request fails, the details are still returned with an empty cast list.
 */
class GetMovieDetailsUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Long): Result<MovieWithCast> = coroutineScope {
        val cast = async { repository.getCast(movieId) }
        repository
            .getMovieDetails(movieId)
            .map { details -> MovieWithCast(details, cast.await().getOrDefault(emptyList())) }
            .also { if (it.isFailure) cast.cancel() }
    }
}

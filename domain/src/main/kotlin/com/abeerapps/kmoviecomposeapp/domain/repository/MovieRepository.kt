package com.abeerapps.kmoviecomposeapp.domain.repository

import androidx.paging.PagingData
import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow

/**
 * Source of movie data. One-shot calls return [Result]; a failure always holds a
 * [com.abeerapps.kmoviecomposeapp.domain.model.DataError].
 */
interface MovieRepository {
    suspend fun getTrendingMovies(): Result<List<Movie>>

    suspend fun getGenres(): Result<List<Genre>>

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>>

    suspend fun getMovieDetails(movieId: Long): Result<MovieDetails>

    suspend fun getCast(movieId: Long): Result<List<CastMember>>
}

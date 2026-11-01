package com.abeerapps.kmoviecomposeapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import com.abeerapps.kmoviecomposeapp.data.mapper.toDomain
import com.abeerapps.kmoviecomposeapp.data.paging.MoviesByGenrePagingSource
import com.abeerapps.kmoviecomposeapp.data.remote.TmdbApi
import com.abeerapps.kmoviecomposeapp.data.remote.safeApiCall
import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails
import com.abeerapps.kmoviecomposeapp.domain.repository.MovieRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl @Inject constructor(private val api: TmdbApi) : MovieRepository {

    override suspend fun getTrendingMovies(): Result<List<Movie>> = safeApiCall {
        api.getTrendingMovies().results.map { it.toDomain() }.distinctBy { it.id }
    }

    override suspend fun getGenres(): Result<List<Genre>> = safeApiCall {
        api.getGenres().genres.map { it.toDomain() }
    }

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        val pager = Pager(
            config = PagingConfig(pageSize = MoviesByGenrePagingSource.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { MoviesByGenrePagingSource(api, genreId) }
        )
        return pager.flow.map { pagingData ->
            // TMDB ranks `discover` results live, so a movie can move to the next page while the
            // user scrolls and arrive twice. Lists are keyed by movie id, so duplicates are dropped.
            val seenIds = ConcurrentHashMap.newKeySet<Long>()
            pagingData.filter { seenIds.add(it.id) }
        }
    }

    override suspend fun getMovieDetails(movieId: Long): Result<MovieDetails> = safeApiCall {
        api.getMovieDetails(movieId).toDomain()
    }

    override suspend fun getCast(movieId: Long): Result<List<CastMember>> = safeApiCall {
        // An actor with several roles appears more than once in the credits.
        api.getCredits(movieId).cast.map { it.toDomain() }.distinctBy { it.id }
    }
}

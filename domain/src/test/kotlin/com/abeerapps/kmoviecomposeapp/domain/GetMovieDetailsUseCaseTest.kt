package com.abeerapps.kmoviecomposeapp.domain

import androidx.paging.PagingData
import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.DataError
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails
import com.abeerapps.kmoviecomposeapp.domain.repository.MovieRepository
import com.abeerapps.kmoviecomposeapp.domain.usecase.GetMovieDetailsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMovieDetailsUseCaseTest {
    private val details = MovieDetails(7, "Title", "Overview", null, 8.0, null, 100, emptyList())
    private val cast = listOf(CastMember(1, "Actor", "Hero", null))

    private class FakeRepository(var details: Result<MovieDetails>, var cast: Result<List<CastMember>>) :
        MovieRepository {
        var requestedId: Long? = null

        override suspend fun getTrendingMovies(): Result<List<Movie>> = error("unused")

        override suspend fun getGenres(): Result<List<Genre>> = error("unused")

        override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> = emptyFlow()

        override suspend fun getMovieDetails(movieId: Long): Result<MovieDetails> = details.also {
            requestedId = movieId
        }

        override suspend fun getCast(movieId: Long): Result<List<CastMember>> = cast
    }

    @Test
    fun `combines details and cast`() = runTest {
        val repository = FakeRepository(Result.success(details), Result.success(cast))

        val result = GetMovieDetailsUseCase(repository)(7).getOrThrow()

        assertEquals(7L, repository.requestedId)
        assertEquals(details, result.details)
        assertEquals(cast, result.cast)
    }

    @Test
    fun `cast failure still returns details with empty cast`() = runTest {
        val repository = FakeRepository(Result.success(details), Result.failure(DataError.Network()))

        val result = GetMovieDetailsUseCase(repository)(7).getOrThrow()

        assertEquals(details, result.details)
        assertTrue(result.cast.isEmpty())
    }

    @Test
    fun `details failure is returned as failure`() = runTest {
        val error = DataError.NotFound()
        val repository = FakeRepository(Result.failure(error), Result.success(cast))

        val result = GetMovieDetailsUseCase(repository)(7)

        assertEquals(error, result.exceptionOrNull())
    }
}

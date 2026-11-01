package com.abeerapps.kmoviecomposeapp.testutil

import androidx.paging.PagingData
import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails
import com.abeerapps.kmoviecomposeapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun testMovie(id: Long, title: String = "Movie $id") = Movie(id, title, "", null, null, 7.0)

/** In-memory [MovieRepository] whose results each test can set. */
class FakeMovieRepository : MovieRepository {
    var trending: Result<List<Movie>> = Result.success(listOf(testMovie(1), testMovie(2)))
    var genres: Result<List<Genre>> = Result.success(listOf(Genre(28, "Action"), Genre(35, "Comedy")))
    var details: Result<MovieDetails> =
        Result.success(MovieDetails(42, "Details", "Overview", null, 8.0, null, 120, emptyList()))
    var cast: Result<List<CastMember>> = Result.success(listOf(CastMember(1, "Actor", null, null)))

    /** Each genre returns movies whose ids start at `genreId * 100`, so tests can tell them apart. */
    var moviesForGenre: (Int) -> List<Movie> = { genreId -> List(3) { testMovie(genreId * 100L + it) } }

    val requestedGenres = mutableListOf<Int>()
    var detailsRequests = 0

    override suspend fun getTrendingMovies() = trending

    override suspend fun getGenres() = genres

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        requestedGenres += genreId
        return flowOf(PagingData.from(moviesForGenre(genreId)))
    }

    override suspend fun getMovieDetails(movieId: Long): Result<MovieDetails> {
        detailsRequests++
        return details.map { it.copy(id = movieId) }
    }

    override suspend fun getCast(movieId: Long) = cast
}

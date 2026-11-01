package com.abeerapps.kmoviecomposeapp.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.abeerapps.kmoviecomposeapp.data.remote.TmdbApi
import com.abeerapps.kmoviecomposeapp.data.remote.dto.CreditsResponseDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.GenresResponseDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.PagedResponseDto
import com.abeerapps.kmoviecomposeapp.domain.model.DataError
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MoviesByGenrePagingSourceTest {
    private class FakeApi(private val totalPages: Int, private val failWith: Exception? = null) : TmdbApi {
        val requests = mutableListOf<Pair<Int, Int>>()

        override suspend fun discoverMovies(genreId: Int, page: Int): PagedResponseDto<MovieDto> {
            requests += genreId to page
            failWith?.let { throw it }
            return PagedResponseDto(page, List(2) { MovieDto(id = page * 10L + it) }, totalPages)
        }

        override suspend fun getTrendingMovies() = error("unused")

        override suspend fun getGenres(): GenresResponseDto = error("unused")

        override suspend fun getMovieDetails(movieId: Long): MovieDetailsDto = error("unused")

        override suspend fun getCredits(movieId: Long): CreditsResponseDto = error("unused")
    }

    private val config = PagingConfig(pageSize = MoviesByGenrePagingSource.PAGE_SIZE)

    @Test
    fun `loads consecutive pages for the genre until the last page`() = runTest {
        val api = FakeApi(totalPages = 2)
        val pager = TestPager(config, MoviesByGenrePagingSource(api, genreId = 28))

        val first = pager.refresh() as LoadResult.Page
        val second = pager.append() as LoadResult.Page

        assertEquals(listOf(28 to 1, 28 to 2), api.requests)
        assertNull(first.prevKey)
        assertEquals(2, first.nextKey)
        assertEquals(listOf(20L, 21L), second.data.map { it.id })
        assertNull("last page has no next key", second.nextKey)
    }

    @Test
    fun `network failure becomes a load error with DataError`() = runTest {
        val pager = TestPager(config, MoviesByGenrePagingSource(FakeApi(1, IOException()), genreId = 28))

        val result = pager.refresh() as LoadResult.Error

        assertTrue(result.throwable is DataError.Network)
    }
}

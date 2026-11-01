package com.abeerapps.kmoviecomposeapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.abeerapps.kmoviecomposeapp.data.mapper.toDomain
import com.abeerapps.kmoviecomposeapp.data.remote.TmdbApi
import com.abeerapps.kmoviecomposeapp.data.remote.toDataError
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import kotlinx.coroutines.CancellationException

class MoviesByGenrePagingSource(private val api: TmdbApi, private val genreId: Int) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val response = api.discoverMovies(genreId = genreId, page = page)
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (page >= minOf(response.totalPages, MAX_PAGE)) null else page + 1
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e.toDataError())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = state.anchorPosition?.let { anchor ->
        val page = state.closestPageToPosition(anchor)
        page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }

    companion object {
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 20

        /** TMDB rejects `discover` requests beyond page 500. */
        const val MAX_PAGE = 500
    }
}

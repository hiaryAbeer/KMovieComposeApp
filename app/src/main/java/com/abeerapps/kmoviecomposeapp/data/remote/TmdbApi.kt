package com.abeerapps.kmoviecomposeapp.data.remote

import com.abeerapps.kmoviecomposeapp.data.remote.dto.CreditsResponseDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.GenresResponseDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.PagedResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** TMDB v3 endpoints (see [TmdbEndpoints]). The API key is added by [ApiKeyInterceptor]. */
interface TmdbApi {
    @GET(TmdbEndpoints.TRENDING_MOVIES)
    suspend fun getTrendingMovies(): PagedResponseDto<MovieDto>

    @GET(TmdbEndpoints.MOVIE_GENRES)
    suspend fun getGenres(): GenresResponseDto

    @GET(TmdbEndpoints.DISCOVER_MOVIES)
    suspend fun discoverMovies(
        @Query(TmdbEndpoints.Query.WITH_GENRES) genreId: Int,
        @Query(TmdbEndpoints.Query.PAGE) page: Int
    ): PagedResponseDto<MovieDto>

    @GET(TmdbEndpoints.MOVIE_DETAILS)
    suspend fun getMovieDetails(@Path(TmdbEndpoints.Path.MOVIE_ID) movieId: Long): MovieDetailsDto

    @GET(TmdbEndpoints.MOVIE_CREDITS)
    suspend fun getCredits(@Path(TmdbEndpoints.Path.MOVIE_ID) movieId: Long): CreditsResponseDto
}

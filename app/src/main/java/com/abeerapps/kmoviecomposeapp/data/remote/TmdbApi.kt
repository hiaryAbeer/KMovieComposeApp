package com.abeerapps.kmoviecomposeapp.data.remote

import com.abeerapps.kmoviecomposeapp.data.remote.dto.CreditsResponseDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.GenresResponseDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.PagedResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** TMDB v3 endpoints. The API key is added by [ApiKeyInterceptor]. */
interface TmdbApi {
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(): PagedResponseDto<MovieDto>

    @GET("genre/movie/list")
    suspend fun getGenres(): GenresResponseDto

    @GET("discover/movie")
    suspend fun discoverMovies(@Query("with_genres") genreId: Int, @Query("page") page: Int): PagedResponseDto<MovieDto>

    @GET("movie/{movieId}")
    suspend fun getMovieDetails(@Path("movieId") movieId: Long): MovieDetailsDto

    @GET("movie/{movieId}/credits")
    suspend fun getCredits(@Path("movieId") movieId: Long): CreditsResponseDto
}

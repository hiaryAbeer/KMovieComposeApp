package com.abeerapps.kmoviecomposeapp.data.remote

/**
 * TMDB v3 paths and parameter names, relative to `BuildConfig.TMDB_BASE_URL`.
 * Values are `const` so they can be used in Retrofit annotations.
 */
object TmdbEndpoints {
    const val TRENDING_MOVIES = "trending/movie/day"
    const val MOVIE_GENRES = "genre/movie/list"
    const val DISCOVER_MOVIES = "discover/movie"
    const val MOVIE_DETAILS = "movie/{${Path.MOVIE_ID}}"
    const val MOVIE_CREDITS = "movie/{${Path.MOVIE_ID}}/credits"

    object Path {
        const val MOVIE_ID = "movieId"
    }

    object Query {
        const val API_KEY = "api_key"
        const val WITH_GENRES = "with_genres"
        const val PAGE = "page"
    }
}

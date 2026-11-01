package com.abeerapps.kmoviecomposeapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/** Adds the TMDB `api_key` query parameter to every request. */
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url =
            request.url
                .newBuilder()
                .addQueryParameter(TmdbEndpoints.Query.API_KEY, apiKey)
                .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}

package com.abeerapps.kmoviecomposeapp.di

import com.abeerapps.kmoviecomposeapp.BuildConfig
import com.abeerapps.kmoviecomposeapp.data.remote.ApiKeyInterceptor
import com.abeerapps.kmoviecomposeapp.data.remote.TmdbApi
import com.abeerapps.kmoviecomposeapp.data.remote.TmdbEndpoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber

/**
 * Hilt scopes determine how long a dependency lives and where it can be injected inside the app
 *
 *. By default, Hilt bindings are unscoped (meaning a new instance is created every time it is requested).
 *  When you add a scope annotation, Hilt shares a single instance of that object throughout the matching
 *  component's lifecycle.
 *
 * @Module is a class used to provide dependencies that cannot be constructor-injected.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val API_KEY_IN_URL = Regex("(${TmdbEndpoints.Query.API_KEY})=[^&\\s]+")

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(ApiKeyInterceptor(BuildConfig.TMDB_API_KEY))
        .apply {
            // Network logging only in debug builds; the key is redacted from logged URLs.
            if (BuildConfig.DEBUG) {
                val logger =
                    HttpLoggingInterceptor.Logger { message ->
                        Timber.tag("OkHttp").d(message.replace(API_KEY_IN_URL, "\$1=***"))
                    }
                addInterceptor(HttpLoggingInterceptor(logger).apply { level = HttpLoggingInterceptor.Level.BASIC })
            }
        }.connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit
        .Builder()
        .baseUrl(BuildConfig.TMDB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideTmdbApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)
}

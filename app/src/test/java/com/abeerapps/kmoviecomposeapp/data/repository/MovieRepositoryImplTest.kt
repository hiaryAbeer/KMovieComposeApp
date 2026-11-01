package com.abeerapps.kmoviecomposeapp.data.repository

import androidx.paging.testing.asSnapshot
import com.abeerapps.kmoviecomposeapp.data.remote.TmdbApi
import com.abeerapps.kmoviecomposeapp.domain.model.DataError
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class MovieRepositoryImplTest {
    private val server = MockWebServer().apply { start() }

    // Same Json configuration as NetworkModule.
    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            explicitNulls = false
        }

    private val repository =
        MovieRepositoryImpl(
            Retrofit
                .Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(TmdbApi::class.java)
        )

    @After
    fun tearDown() = server.shutdown()

    private fun respond(body: String, code: Int = 200) =
        server.enqueue(MockResponse().setResponseCode(code).setBody(body))

    @Test
    fun `trending maps results and tolerates null and missing fields`() = runTest {
        respond(
            """{"page":1,"total_pages":1,"results":[
                    {"id":1,"title":"Dune","poster_path":"/dune.jpg","vote_average":8.1,"extra":"ignored"},
                    {"id":2,"title":null,"poster_path":"","vote_average":null}
                ]}"""
        )

        val movies = repository.getTrendingMovies().getOrThrow()

        assertEquals("/trending/movie/day", server.takeRequest().requestUrl!!.encodedPath)
        assertEquals(2, movies.size)
        assertEquals("Dune", movies[0].title)
        assertEquals(8.1, movies[0].voteAverage, 0.0)
        assertEquals("", movies[1].title)
        assertNull(movies[1].posterPath)
        assertEquals(0.0, movies[1].voteAverage, 0.0)
    }

    @Test
    fun `details and credits are requested for the given movie`() = runTest {
        respond("""{"id":7,"title":"Seven","runtime":0,"genres":[{"id":28,"name":"Action"}]}""")
        respond("""{"cast":[{"id":3,"name":"Actor","character":"Hero"}]}""")

        val details = repository.getMovieDetails(7).getOrThrow()
        val cast = repository.getCast(7).getOrThrow()

        assertEquals("/movie/7", server.takeRequest().requestUrl!!.encodedPath)
        assertEquals("/movie/7/credits", server.takeRequest().requestUrl!!.encodedPath)
        assertEquals("Seven", details.title)
        assertNull(details.runtimeMinutes)
        assertEquals("Action", details.genres.single().name)
        assertEquals("Hero", cast.single().character)
    }

    @Test
    fun `movies repeated across genre pages appear only once`() = runTest {
        respond("""{"page":1,"total_pages":2,"results":[{"id":1},{"id":2}]}""")
        respond("""{"page":2,"total_pages":2,"results":[{"id":2},{"id":3}]}""")

        val ids = repository.getMoviesByGenre(28).asSnapshot { scrollTo(index = 3) }.map { it.id }

        assertEquals(listOf(1L, 2L, 3L), ids)
    }

    @Test
    fun `actor with several roles appears once in the cast`() = runTest {
        respond("""{"cast":[{"id":3,"name":"Actor","character":"A"},{"id":3,"name":"Actor","character":"B"}]}""")

        assertEquals(1, repository.getCast(1).getOrThrow().size)
    }

    @Test
    fun `401 maps to Unauthorized`() = runTest {
        respond("""{"status_message":"Invalid API key"}""", code = 401)

        assertTrue(repository.getGenres().exceptionOrNull() is DataError.Unauthorized)
    }

    @Test
    fun `404 maps to NotFound`() = runTest {
        respond("{}", code = 404)

        assertTrue(repository.getMovieDetails(1).exceptionOrNull() is DataError.NotFound)
    }

    @Test
    fun `500 maps to Unknown`() = runTest {
        respond("{}", code = 500)

        assertTrue(repository.getTrendingMovies().exceptionOrNull() is DataError.Unknown)
    }

    @Test
    fun `connection failure maps to Network`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        assertTrue(repository.getGenres().exceptionOrNull() is DataError.Network)
    }

    @Test
    fun `malformed body maps to Unknown`() = runTest {
        respond("not json")

        assertTrue(repository.getGenres().exceptionOrNull() is DataError.Unknown)
    }
}

package com.abeerapps.kmoviecomposeapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiKeyInterceptorTest {
    private val server = MockWebServer().apply { start() }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `adds api key while keeping existing query parameters`() {
        server.enqueue(MockResponse())
        val client = OkHttpClient.Builder().addInterceptor(ApiKeyInterceptor("secret")).build()

        client.newCall(Request.Builder().url(server.url("/discover/movie?page=2")).build()).execute().close()

        val url = server.takeRequest().requestUrl!!
        assertEquals("secret", url.queryParameter("api_key"))
        assertEquals("2", url.queryParameter("page"))
    }
}

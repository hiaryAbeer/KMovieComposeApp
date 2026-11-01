package com.abeerapps.kmoviecomposeapp.data.mapper

import com.abeerapps.kmoviecomposeapp.data.remote.dto.CastDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.GenreDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMappersTest {
    @Test
    fun `movie with missing fields gets safe defaults`() {
        val movie = MovieDto(id = 1).toDomain()

        assertEquals("", movie.title)
        assertEquals("", movie.overview)
        assertEquals(0.0, movie.voteAverage, 0.0)
        assertNull(movie.posterPath)
    }

    @Test
    fun `blank paths become null so no broken image url is built`() {
        assertNull(MovieDto(id = 1, posterPath = " ").toDomain().posterPath)
        assertNull(CastDto(id = 1, profilePath = "").toDomain().profilePath)
    }

    @Test
    fun `details maps genres and drops zero runtime`() {
        val details =
            MovieDetailsDto(id = 5, title = "T", runtime = 0, genres = listOf(GenreDto(28, "Action"))).toDomain()

        assertEquals("Action", details.genres.single().name)
        assertNull(details.runtimeMinutes)
        assertEquals(95, MovieDetailsDto(id = 5, runtime = 95).toDomain().runtimeMinutes)
    }
}

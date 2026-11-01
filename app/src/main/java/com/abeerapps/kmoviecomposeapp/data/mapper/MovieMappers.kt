package com.abeerapps.kmoviecomposeapp.data.mapper

import com.abeerapps.kmoviecomposeapp.data.remote.dto.CastDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.GenreDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDetailsDto
import com.abeerapps.kmoviecomposeapp.data.remote.dto.MovieDto
import com.abeerapps.kmoviecomposeapp.domain.model.CastMember
import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.model.MovieDetails

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title.orEmpty(),
    overview = overview.orEmpty(),
    posterPath = posterPath?.takeIf { it.isNotBlank() },
    releaseDate = releaseDate?.takeIf { it.isNotBlank() },
    voteAverage = voteAverage ?: 0.0
)

fun GenreDto.toDomain(): Genre = Genre(id = id, name = name.orEmpty())

fun MovieDetailsDto.toDomain(): MovieDetails = MovieDetails(
    id = id,
    title = title.orEmpty(),
    overview = overview.orEmpty(),
    posterPath = posterPath?.takeIf { it.isNotBlank() },
    voteAverage = voteAverage ?: 0.0,
    releaseDate = releaseDate?.takeIf { it.isNotBlank() },
    runtimeMinutes = runtime?.takeIf { it > 0 },
    genres = genres.map { it.toDomain() }
)

fun CastDto.toDomain(): CastMember = CastMember(
    id = id,
    name = name.orEmpty(),
    character = character?.takeIf { it.isNotBlank() },
    profilePath = profilePath?.takeIf { it.isNotBlank() }
)

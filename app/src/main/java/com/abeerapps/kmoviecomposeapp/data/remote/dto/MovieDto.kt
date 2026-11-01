package com.abeerapps.kmoviecomposeapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTOs mirror the API exactly and are nullable wherever TMDB may omit or null a field;
// mappers turn them into the non-null domain models.

@Serializable
data class MovieDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null
)

@Serializable
data class GenreDto(@SerialName("id") val id: Int, @SerialName("name") val name: String? = null)

@Serializable
data class GenresResponseDto(@SerialName("genres") val genres: List<GenreDto> = emptyList())

@Serializable
data class MovieDetailsDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList()
)

@Serializable
data class CastDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String? = null,
    @SerialName("character") val character: String? = null,
    @SerialName("profile_path") val profilePath: String? = null
)

@Serializable
data class CreditsResponseDto(@SerialName("cast") val cast: List<CastDto> = emptyList())

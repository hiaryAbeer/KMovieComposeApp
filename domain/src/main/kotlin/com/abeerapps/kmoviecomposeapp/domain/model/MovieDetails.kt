package com.abeerapps.kmoviecomposeapp.domain.model

data class MovieDetails(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String?,
    val runtimeMinutes: Int?,
    val genres: List<Genre>
)

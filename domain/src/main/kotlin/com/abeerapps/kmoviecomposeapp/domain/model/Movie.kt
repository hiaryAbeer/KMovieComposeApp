package com.abeerapps.kmoviecomposeapp.domain.model

data class Movie(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String?,
    val voteAverage: Double
)

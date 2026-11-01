package com.abeerapps.kmoviecomposeapp.domain.usecase

import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetTrendingMoviesUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(): Result<List<Movie>> = repository.getTrendingMovies()
}

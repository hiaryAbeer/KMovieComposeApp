package com.abeerapps.kmoviecomposeapp.domain.usecase

import com.abeerapps.kmoviecomposeapp.domain.model.Genre
import com.abeerapps.kmoviecomposeapp.domain.repository.MovieRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(): Result<List<Genre>> = repository.getGenres()
}

package com.abeerapps.kmoviecomposeapp.domain.usecase

import androidx.paging.PagingData
import com.abeerapps.kmoviecomposeapp.domain.model.Movie
import com.abeerapps.kmoviecomposeapp.domain.repository.MovieRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetMoviesByGenreUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke(genreId: Int): Flow<PagingData<Movie>> = repository.getMoviesByGenre(genreId)
}

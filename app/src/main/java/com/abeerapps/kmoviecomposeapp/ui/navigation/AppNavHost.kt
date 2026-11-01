package com.abeerapps.kmoviecomposeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abeerapps.kmoviecomposeapp.ui.details.MovieDetailsRoute
import com.abeerapps.kmoviecomposeapp.ui.movies.MoviesRoute
import kotlinx.serialization.Serializable

@Serializable
data object MoviesDestination

@Serializable
data class MovieDetailsDestination(val movieId: Long) {
    companion object {
        /** Must match the property name above; type-safe navigation stores arguments under it. */
        const val ARG_MOVIE_ID = "movieId"
    }
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MoviesDestination, modifier = modifier) {
        composable<MoviesDestination> {
            MoviesRoute(onMovieClick = { navController.navigate(MovieDetailsDestination(it)) })
        }
        composable<MovieDetailsDestination> {
            MovieDetailsRoute(onBack = navController::navigateUp)
        }
    }
}

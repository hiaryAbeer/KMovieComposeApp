package com.abeerapps.kmoviecomposeapp.ui.common

import com.abeerapps.kmoviecomposeapp.domain.model.DataError

/** Loading, loaded or failed content for one section of a screen. */
sealed interface ContentState<out T> {
    data object Loading : ContentState<Nothing>

    data class Success<T>(val data: T) : ContentState<T>

    data class Error(val error: DataError) : ContentState<Nothing>
}

fun <T> Result<T>.toContentState(): ContentState<T> = fold(
    onSuccess = { ContentState.Success(it) },
    onFailure = { ContentState.Error(it as? DataError ?: DataError.Unknown(it)) }
)

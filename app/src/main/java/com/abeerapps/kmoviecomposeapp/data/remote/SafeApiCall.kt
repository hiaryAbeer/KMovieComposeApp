package com.abeerapps.kmoviecomposeapp.data.remote

import com.abeerapps.kmoviecomposeapp.domain.model.DataError
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import timber.log.Timber

/**
 * Runs [block] and wraps the outcome in a [Result] whose failure is always a [DataError].
 * Coroutine cancellation is rethrown so it is never mistaken for an error.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Timber.w(e, "API call failed")
    Result.failure(e.toDataError())
}

fun Throwable.toDataError(): DataError = when (this) {
    is DataError -> this
    is IOException -> DataError.Network(this)
    is HttpException ->
        when (code()) {
            401 -> DataError.Unauthorized(this)
            404 -> DataError.NotFound(this)
            else -> DataError.Unknown(this)
        }
    is SerializationException -> DataError.Unknown(this)
    else -> DataError.Unknown(this)
}

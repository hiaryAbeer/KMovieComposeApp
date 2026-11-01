package com.abeerapps.kmoviecomposeapp.domain.model

/**
 * Failures the data layer reports to the rest of the app, independent of the
 * networking library that produced them.
 */
sealed class DataError(cause: Throwable? = null) : Exception(cause) {
    /** No connection, DNS failure or timeout. Usually fixed by retrying. */
    class Network(cause: Throwable? = null) : DataError(cause)

    /** The API key is missing or invalid (HTTP 401). */
    class Unauthorized(cause: Throwable? = null) : DataError(cause)

    /** The requested item does not exist (HTTP 404). */
    class NotFound(cause: Throwable? = null) : DataError(cause)

    /** Any other server error or an unreadable response. */
    class Unknown(cause: Throwable? = null) : DataError(cause)
}

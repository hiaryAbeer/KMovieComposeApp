package com.abeerapps.kmoviecomposeapp.ui.common

import androidx.annotation.StringRes
import com.abeerapps.kmoviecomposeapp.R
import com.abeerapps.kmoviecomposeapp.domain.model.DataError

@StringRes
fun Throwable.messageRes(): Int = when (this) {
    is DataError.Network -> R.string.error_network
    is DataError.Unauthorized -> R.string.error_unauthorized
    is DataError.NotFound -> R.string.error_not_found
    else -> R.string.error_unknown
}

package com.abeerapps.kmoviecomposeapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResponseDto<T>(
    @SerialName("page") val page: Int = 1,
    @SerialName("results") val results: List<T> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 1
)

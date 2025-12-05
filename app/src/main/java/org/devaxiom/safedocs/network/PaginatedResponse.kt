package org.devaxiom.safedocs.network

import com.google.gson.annotations.SerializedName

/**
 * A generic class to handle paginated responses from the backend.
 */
data class PaginatedResponse<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("total") val total: Long
)

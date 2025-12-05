package org.devaxiom.safedocs.network

import com.google.gson.annotations.SerializedName

/**
 * A generic wrapper for all successful API responses from the backend.
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)

package org.devaxiom.safedocs.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String, // Corrected from Long to String
    @SerializedName("publicId") val publicId: String,
    @SerializedName("email") val email: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("fullName") val fullName: String
)

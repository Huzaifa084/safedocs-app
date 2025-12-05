package org.devaxiom.safedocs.network

import com.google.gson.annotations.SerializedName
import org.devaxiom.safedocs.data.model.User

data class LoginData(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("expiresInSeconds") val expiresInSeconds: Long,
    @SerializedName("user") val user: User
)

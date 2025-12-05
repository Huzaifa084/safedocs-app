package org.devaxiom.safedocs.network

import com.google.gson.annotations.SerializedName

// The backend expects a field named "idToken", but it's good practice in Kotlin
// to use camelCase for properties. @SerializedName handles the mapping.
data class GoogleLoginRequest(@SerializedName("idToken") val idToken: String)

package org.devaxiom.safedocs.auth

data class GoogleLoginRequest(
    val idToken: String
)

data class AuthResponse(
    val token: String?,
    val userId: String?,
    val email: String?
)

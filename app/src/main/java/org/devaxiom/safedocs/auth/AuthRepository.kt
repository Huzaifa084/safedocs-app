package org.devaxiom.safedocs.auth

import org.devaxiom.safedocs.network.ApiClient

class AuthRepository {
    private val api = ApiClient.retrofit.create(AuthApi::class.java)

    suspend fun loginWithGoogle(idToken: String): AuthResponse {
        return api.loginWithGoogle(GoogleLoginRequest(idToken))
    }
}

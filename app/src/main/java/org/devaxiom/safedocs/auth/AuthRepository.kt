package org.devaxiom.safedocs.auth

import org.devaxiom.safedocs.network.ApiClient
import org.devaxiom.safedocs.network.GoogleLoginRequest
import org.devaxiom.safedocs.network.LoginResponse
import retrofit2.Response

class AuthRepository {
    private val apiService = ApiClient.instance

    suspend fun loginWithGoogle(idToken: String): Response<LoginResponse> {
        return apiService.loginWithGoogle(GoogleLoginRequest(idToken))
    }
}

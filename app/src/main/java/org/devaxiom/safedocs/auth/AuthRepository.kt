package org.devaxiom.safedocs.auth

import org.devaxiom.safedocs.network.ApiClient
import org.devaxiom.safedocs.network.ApiResponse
import org.devaxiom.safedocs.network.GoogleLoginRequest
import org.devaxiom.safedocs.network.LoginData
import retrofit2.Response

class AuthRepository {
    private val apiService = ApiClient.instance

    suspend fun loginWithGoogle(idToken: String): Response<ApiResponse<LoginData>> {
        return apiService.loginWithGoogle(GoogleLoginRequest(idToken))
    }
}

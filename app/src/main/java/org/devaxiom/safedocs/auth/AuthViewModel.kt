package org.devaxiom.safedocs.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.security.TokenManager
import org.devaxiom.safedocs.data.security.UserManager
import org.devaxiom.safedocs.data.security.SessionManager
import org.devaxiom.safedocs.data.auth.AuthState

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val userManager = UserManager(application)
    private val sessionManager = SessionManager(application)
    private val authRepository = AuthRepository()

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.loginWithGoogle(idToken)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { data ->
                        tokenManager.saveToken(data.accessToken)
                        userManager.saveUser(data.user)
                        // Start authenticated session for consistent app-wide checks
                        sessionManager.startAuthenticatedSession(data.accessToken, data.user)
                        // Broadcast authenticated state
                        AuthState.setAuthenticated()
                        onSuccess()
                    } ?: onError("User data or token was null in the response.")
                } else {
                    val errorMessage = response.body()?.message ?: response.message() ?: "Unknown error"
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        // Clear local session
        tokenManager.clearToken()
        userManager.clearUser()
        sessionManager.endSession()
        AuthState.setGuest()
        onSuccess()
    }
}

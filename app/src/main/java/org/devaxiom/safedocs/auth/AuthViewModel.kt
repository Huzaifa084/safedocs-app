package org.devaxiom.safedocs.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.security.TokenManager
import org.devaxiom.safedocs.network.GoogleLoginRequest

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val authRepository = AuthRepository()

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.loginWithGoogle(idToken)
                if (response.isSuccessful) {
                    response.body()?.token?.let {
                        tokenManager.saveToken(it)
                        onSuccess()
                    }
                } else {
                    onError(response.message() ?: "Unknown error")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }
}

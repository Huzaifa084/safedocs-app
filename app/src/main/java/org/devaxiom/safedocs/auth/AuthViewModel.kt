package org.devaxiom.safedocs.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.devaxiom.safedocs.data.security.TokenManager

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val authRepository = AuthRepository()

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = authRepository.loginWithGoogle(idToken)
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.accessToken?.let {
                        tokenManager.saveToken(it)
                        onSuccess()
                    } ?: onError("Access token was null in the response.")
                } else {
                    val errorMessage = response.body()?.message ?: response.message() ?: "Unknown error"
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }
}

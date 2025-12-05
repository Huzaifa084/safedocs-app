package org.devaxiom.safedocs.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    fun loginWithGoogle(idToken: String, onSuccess: (AuthResponse) -> Unit, onError: (Throwable) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = repo.loginWithGoogle(idToken)
                onSuccess(resp)
            } catch (ce: CancellationException) {
                // ignore
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }
}

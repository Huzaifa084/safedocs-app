package org.devaxiom.safedocs.data.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.devaxiom.safedocs.data.security.SessionManager

/**
 * Tiny observable auth state for global guest checks.
 */
object AuthState {
    private val _isGuest = MutableLiveData<Boolean>(true)
    val isGuest: LiveData<Boolean> = _isGuest

    fun init(context: Context) {
        val sessionManager = SessionManager(context)
        _isGuest.value = sessionManager.isGuest()
    }

    fun setGuest() {
        _isGuest.value = true
    }

    fun setAuthenticated() {
        _isGuest.value = false
    }
}

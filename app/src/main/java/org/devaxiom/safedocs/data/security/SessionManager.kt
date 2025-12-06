package org.devaxiom.safedocs.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.devaxiom.safedocs.data.model.User

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val USER_FULL_NAME = "USER_FULL_NAME"
    }

    fun startAuthenticatedSession(token: String, user: User) {
        sharedPreferences.edit()
            .putString(AUTH_TOKEN, token)
            .putString(USER_EMAIL, user.email)
            .putString(USER_FULL_NAME, user.fullName)
            .apply()
    }

    fun endSession() {
        sharedPreferences.edit().clear().apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(AUTH_TOKEN, null)
    }

    fun isGuest(): Boolean {
        return getAuthToken() == null
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(USER_EMAIL, null)
    }

    fun getUserFullName(): String? {
        return sharedPreferences.getString(USER_FULL_NAME, null)
    }
}

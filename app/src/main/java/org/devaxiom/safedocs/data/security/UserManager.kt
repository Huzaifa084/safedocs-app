package org.devaxiom.safedocs.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.devaxiom.safedocs.data.model.User

class UserManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "user_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUser(user: User) {
        sharedPreferences.edit()
            .putString("USER_EMAIL", user.email)
            .putString("USER_FULL_NAME", user.fullName)
            .apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("USER_EMAIL", null)
    }

    fun getUserFullName(): String? {
        return sharedPreferences.getString("USER_FULL_NAME", null)
    }

    fun clearUser() {
        sharedPreferences.edit().clear().apply()
    }
}

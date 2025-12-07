package org.devaxiom.safedocs.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import org.devaxiom.safedocs.data.security.SessionManager
import org.devaxiom.safedocs.events.AuthEventBus

class ResponseInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 401 || response.code == 400) {
            // Mark as unauthenticated for UI to react
            val sessionManager = SessionManager(context)
            if (sessionManager.isGuest()) {
                // Broadcast unauthenticated state for UI to show guest prompts
                AuthEventBus.notifyUnauthenticated()
            }
        }
        return response
    }
}

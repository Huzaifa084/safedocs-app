package org.devaxiom.safedocs.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import org.devaxiom.safedocs.data.security.TokenManager

class AuthInterceptor(context: Context) : Interceptor {

    private val tokenManager = TokenManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        tokenManager.getToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(requestBuilder.build())
    }
}

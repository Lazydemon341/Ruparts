package com.ruparts.app.core.network.interceptor

import android.util.Log
import com.ruparts.app.core.data.local.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp interceptor that adds the authentication token to all requests
 */
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("MY_TAG", "HEllo from interceptor")
        val originalRequest = chain.request()

        // If no token is available, proceed with the original request
        val token = runBlocking { tokenStorage.getToken() }
            ?: return chain.proceed(originalRequest)

        // Add the Authorization header with the Bearer token
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}

package com.ruparts.app.core.network.interceptor

import com.ruparts.app.core.data.local.TokenStorage
import com.ruparts.app.core.navigation.NavigationManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp interceptor that adds the authentication token to all requests
 */
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val navigationManager: NavigationManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // If no token is available, proceed with the original request
        val token = runBlocking { tokenStorage.getToken() }
            ?: return chain.proceed(originalRequest)

        // Add the Authorization header with the Bearer token
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        val response =  chain.proceed(newRequest)

        // Handle token expiration
        if (response.code == 401) {
            runBlocking {
                tokenStorage.clearToken()
                navigationManager.navigateToAuth(showAuthError = true)
            }
        }

        return response
    }
}

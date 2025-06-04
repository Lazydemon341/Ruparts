package com.ruparts.app.features.authorization.data.repository

import com.ruparts.app.core.data.local.TokenStorage
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.authorization.data.network.AuthRetrofitService
import com.ruparts.app.features.authorization.data.network.model.AuthRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRetrofitService: AuthRetrofitService,
    private val tokenStorage: TokenStorage,
) {
    suspend fun loginWithPinCode(pinCode: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCoroutineCatching {
            val authResponse = authRetrofitService.loginByCode(AuthRequest(code = pinCode))
            tokenStorage.saveToken(authResponse.token)
        }
    }

    suspend fun isAuthenticated(): Boolean {
        return tokenStorage.hasToken()
    }

    suspend fun logout() {
        tokenStorage.clearToken()
    }
}

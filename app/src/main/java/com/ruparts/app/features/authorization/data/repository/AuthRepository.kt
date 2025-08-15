package com.ruparts.app.features.authorization.data.repository

import com.ruparts.app.core.data.local.TokenStorage
import com.ruparts.app.core.model.User
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.authorization.data.network.AuthRetrofitService
import com.ruparts.app.features.authorization.data.network.model.AuthRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRetrofitService: AuthRetrofitService,
    private val tokenStorage: TokenStorage,
) {

    private val dateFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx")
    }

    fun isAuthenticatedFlow(): Flow<Boolean> {
        return tokenStorage.tokenFlow
            .map { !it.isNullOrEmpty() }
    }

    fun userFlow(): Flow<User?> {
        return isAuthenticatedFlow()
            .map { isAuthenticated ->
                if (isAuthenticated) {
                    getUser().getOrThrow()
                } else {
                    null
                }
            }
            .retry(3)
            .catch { emit(null) }
    }

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

    suspend fun getUser(): Result<User> = withContext(Dispatchers.Default) {
        runCoroutineCatching {
            val userResponse = authRetrofitService.getUser()
            userResponse.mapToUser(dateFormatter)
        }
    }
}

package com.ruparts.app.core.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage for the authentication token using EncryptedSharedPreferences
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        KEY_AUTH_PREFS,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)

    /**
     * Save the authentication token
     */
    suspend fun saveToken(token: String) = withContext(dispatcher) {
        sharedPreferences.edit() { putString(KEY_AUTH_TOKEN, token) }
    }

    /**
     * Get the saved authentication token
     * @return The token or null if not found
     */
    suspend fun getToken(): String? = withContext(dispatcher) {
        sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Clear the saved authentication token
     */
    suspend fun clearToken() = withContext(dispatcher) {
        sharedPreferences.edit() { remove(KEY_AUTH_TOKEN) }
    }

    /**
     * Check if a token exists
     */
    suspend fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    companion object {
        private const val KEY_AUTH_PREFS = "auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}

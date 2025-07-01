package com.ruparts.app.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenCryptoManager: TokenCryptoManager,
) {

    private val Context.dataStore by preferencesDataStore(name = KEY_AUTH_PREFS)
    private val dataStore get() = context.dataStore

    private val keyAuthToken = stringPreferencesKey(KEY_AUTH_TOKEN)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = Dispatchers.IO.limitedParallelism(1)


    val tokenFlow: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[keyAuthToken]?.let { encryptedString ->
                tokenCryptoManager.decrypt(KEY_ALIAS, encryptedString)
            }
        }

    suspend fun saveToken(token: String) {
        withContext(dispatcher) {
            val encrypted = tokenCryptoManager.encrypt(KEY_ALIAS, token)
            dataStore.edit { preferences ->
                preferences[keyAuthToken] = encrypted
            }
        }
    }

    suspend fun getToken(): String? {
        return withContext(dispatcher) {
            tokenFlow.firstOrNull()
        }
    }

    suspend fun clearToken() = withContext(dispatcher) {
        dataStore.edit { preferences ->
            preferences.remove(keyAuthToken)
        }
    }

    suspend fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    companion object {
        private const val KEY_AUTH_PREFS = "auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_ALIAS = "token_key_alias"
    }
}

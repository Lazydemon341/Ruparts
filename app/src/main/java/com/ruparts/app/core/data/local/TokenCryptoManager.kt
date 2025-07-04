package com.ruparts.app.core.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

private const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val TRANSFORMATION = "AES/GCM/NoPadding"
private const val IV_SEPARATOR = ":iv:"
private const val KEY_ALIAS = "token_key_alias"

class TokenCryptoManager @Inject constructor() {

    private val keyStore by lazy {
        KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
    }

    fun encrypt(plainText: String): String {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val encryptedBase64 = Base64.encodeToString(encrypted, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        return "$ivBase64$IV_SEPARATOR$encryptedBase64"
    }

    fun decrypt(encryptedString: String): String {
        val (ivBase64, encryptedBase64) = encryptedString.split(IV_SEPARATOR, limit = 2)
        val iv = Base64.decode(ivBase64, Base64.DEFAULT)
        val encrypted = Base64.decode(encryptedBase64, Base64.DEFAULT)
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        return keyGenerator.generateKey()
    }
} 
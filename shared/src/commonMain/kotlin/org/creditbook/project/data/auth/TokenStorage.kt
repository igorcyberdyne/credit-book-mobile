package org.creditbook.project.data.auth

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val KEY_JWT_TOKEN = "jwt_token"
private const val KEY_JWT_SAVE_AT = "jwt_save_at"
private const val JWT_TOKEN_EXPIRES_IN = 3600L

class TokenStorage(
    private val settings: Settings
) {

    @OptIn(ExperimentalTime::class)
    fun saveToken(token: String) {
        settings[KEY_JWT_TOKEN] = token
        settings[KEY_JWT_SAVE_AT] = Clock.System.now().epochSeconds

    }

    @OptIn(ExperimentalTime::class)
    private fun isTokenExpired(token: String?): Boolean {
        if (token == null) {
            return true
        }

        val savedAt = settings.getLongOrNull(KEY_JWT_SAVE_AT) ?: return true

        val expirationTime = savedAt + JWT_TOKEN_EXPIRES_IN

        return Clock.System.now().epochSeconds >= expirationTime

    }

    fun getToken(): String? {
        val token: String? = settings.getStringOrNull(KEY_JWT_TOKEN)

        if (isTokenExpired(token)) {
            return null
        }

        return token;
    }

    fun clearToken() {
        settings.remove(KEY_JWT_TOKEN)
        settings.remove(KEY_JWT_SAVE_AT)
    }
}
package org.creditbook.project.data.auth

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val KEY_JWT_TOKEN = "jwt_token"
private const val KEY_JWT_SAVE_AT = "jwt_save_at"
private const val JWT_TOKEN_EXPIRES_IN = 3600L

private const val KEY_TOKEN = "jwt_token"
private const val KEY_REFRESH_TOKEN = "jwt_refresh_token"
private const val KEY_EXPIRES_AT = "jwt_expires_at"

class TokenStorage(
    private val settings: Settings
) {

    @OptIn(ExperimentalTime::class)
    fun saveToken(token: String) {
        settings[KEY_JWT_TOKEN] = token
        settings[KEY_JWT_SAVE_AT] = Clock.System.now().epochSeconds

    }

    @OptIn(ExperimentalTime::class)
    suspend fun saveTokens(token: String, refreshToken: String, expiresInSeconds: Long) {
        settings[KEY_TOKEN] = token
        settings[KEY_REFRESH_TOKEN] = refreshToken
        settings[KEY_EXPIRES_AT] = Clock.System.now().epochSeconds + expiresInSeconds
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

    suspend fun getToken(): String? = settings.getStringOrNull(KEY_TOKEN)

    suspend fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH_TOKEN)

    fun clearTokens() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_EXPIRES_AT)
    }
}
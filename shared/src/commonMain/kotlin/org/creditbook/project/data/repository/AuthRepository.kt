package org.creditbook.project.data.repository

import org.creditbook.project.data.auth.TokenStorage
import org.creditbook.project.data.remote.dto.LoginCommand
import org.creditbook.project.data.remote.dto.LoginResponse
import org.creditbook.project.model.User
import org.creditbook.project.model.toDomain
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.creditbook.project.data.local.SessionDatabase
import org.creditbook.project.data.remote.dto.ApiResponse
import org.creditbook.project.data.remote.dto.OnboardingCommand
import org.creditbook.project.model.Session

class AuthRepository(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
    private val sessionDatabase: SessionDatabase
) {
    suspend fun login(email: String, password: String): User {
        val response = httpClient.post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginCommand(email, password))
        }.body<ApiResponse<LoginResponse>>().data

        tokenStorage.saveTokens(response.token, response.refreshToken, response.expiresIn)

        sessionDatabase.saveSession(response)

        return response.user.toDomain()
    }

    suspend fun onboard(request: OnboardingCommand): User {
        val response = httpClient.post("/api/onboarding") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<ApiResponse<LoginResponse>>().data

        tokenStorage.saveTokens(response.token, response.refreshToken, response.expiresIn)

        sessionDatabase.saveSession(response)

        return response.user.toDomain()
    }

    fun getCachedSession(): Session {
        return sessionDatabase.getCachedSession()
    }

    fun logout() {
        sessionDatabase.clearSession()
        tokenStorage.clearTokens()
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null && sessionDatabase.hasSession()
    }
}
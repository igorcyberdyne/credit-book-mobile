package org.creditbook.project.data.repository

import org.creditbook.project.data.auth.TokenStorage
import org.creditbook.project.data.remote.dto.LoginCommand
import org.creditbook.project.data.remote.dto.LoginResponse
import org.creditbook.project.model.User
import org.creditbook.project.model.toDomain
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.*
import io.ktor.http.*
import org.creditbook.project.data.remote.dto.ApiResponse

class AuthRepository(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage
) {
    suspend fun login(email: String, password: String): User {
        val response = httpClient.post("/api/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginCommand(email, password))
        }.body<ApiResponse<LoginResponse>>().data

        tokenStorage.saveToken(response.token)

        return response.user.toDomain()
    }

    suspend fun logout() {
        tokenStorage.clearToken()
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }
}
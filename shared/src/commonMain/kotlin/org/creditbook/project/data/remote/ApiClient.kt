package org.creditbook.project.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.plugin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.creditbook.project.data.auth.TokenStorage
import org.creditbook.project.data.remote.dto.ApiErrorData
import org.creditbook.project.data.remote.dto.ApiErrorResponse
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.remote.dto.ApiResponse
import org.creditbook.project.data.remote.dto.LexikJwtErrorDto
import org.creditbook.project.data.remote.dto.LoginResponse
import org.creditbook.project.data.remote.dto.RefreshTokenCommand

expect fun httpClientEngine(): HttpClientEngineFactory<*>

private val CODES_REQUIRING_LOGOUT = setOf(
    "INVALID_TOKEN", "TOKEN_MISSING", "REFRESH_TOKEN_NOT_FOUND", "INVALID_REFRESH_TOKEN"
)

fun createHttpClient(
    baseUrl: String,
    tokenStorage: TokenStorage,
    onUnauthorized: suspend () -> Unit
): HttpClient {

    val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    // Client dédié au refresh, séparé du client principal pour éviter toute
    // récursion si l'appel de refresh échouait lui-même avec un code géré ci-dessous.
    val refreshClient = HttpClient(httpClientEngine()) {
        defaultRequest { url(baseUrl) }
        install(ContentNegotiation) { json(json) }
        expectSuccess = false
    }

    suspend fun parseError(response: HttpResponse): ApiErrorData? {
        val raw = try {
            response.bodyAsText()
        } catch (e: Exception) {
            return null
        }

        val appError = try {
            json.decodeFromString<ApiErrorResponse>(raw).error
        } catch (e: Exception) {
            null
        }

        return appError ?: try {
            val jwtError = json.decodeFromString<LexikJwtErrorDto>(raw)
            ApiErrorData(code = JsonPrimitive(jwtError.code), message = jwtError.message)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun tryRefreshToken(): Boolean {
        val refreshToken = tokenStorage.getRefreshToken() ?: return false
        return try {
            val response = refreshClient.post("/api/token/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenCommand(refreshToken))
            }
            if (response.status.isSuccess()) {
                val data = response.body<ApiResponse<LoginResponse>>().data
                tokenStorage.saveTokens(data.token, data.refreshToken, data.expiresIn)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun throwApiException(error: ApiErrorData?, status: HttpStatusCode): Nothing {
        throw ApiException(
            code = error?.codeAsString ?: status.value.toString(),
            message = error?.message ?: "Une erreur est survenue",
            details = error?.details ?: emptyList()
        )
    }

    val client = HttpClient(httpClientEngine()) {
        defaultRequest { url(baseUrl) }

        install(ContentNegotiation) { json(json) }

        // TODO DELETE LOG
        // install(Logging) { level = LogLevel.ALL }

        // Géré manuellement dans l'intercepteur ci-dessous, pour pouvoir
        // inspecter le code d'erreur avant de décider (retry vs déconnexion).
        expectSuccess = false
    }

    client.plugin(HttpSend).intercept { request ->
        suspend fun attachToken() {
            request.headers.remove(HttpHeaders.Authorization)
            tokenStorage.getToken()
                ?.let { request.headers.append(HttpHeaders.Authorization, "Bearer $it") }
        }

        attachToken()
        var call = execute(request)

        if (!call.response.status.isSuccess()) {
            val error = parseError(call.response)

            when (error?.codeAsString) {
                "TOKEN_EXPIRED" -> {
                    if (tryRefreshToken()) {
                        attachToken()
                        call = execute(request)
                        if (!call.response.status.isSuccess()) {
                            throwApiException(parseError(call.response), call.response.status)
                        }
                    } else {
                        onUnauthorized()
                        throwApiException(error, call.response.status)
                    }
                }

                in CODES_REQUIRING_LOGOUT -> {
                    onUnauthorized()
                    throwApiException(error, call.response.status)
                }

                else -> {
                    throwApiException(error, call.response.status)
                }
            }
        }

        call
    }

    return client
}
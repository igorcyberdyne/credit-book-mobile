package org.creditbook.project.data.remote
import io.ktor.client.*
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.creditbook.project.data.auth.TokenStorage
import org.creditbook.project.data.remote.dto.ApiErrorResponse
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.remote.dto.LexikJwtErrorDto

expect fun httpClientEngine(): HttpClientEngineFactory<*>

fun createHttpClient(
    baseUrl: String,
    tokenStorage: TokenStorage,
    onUnauthorized: suspend () -> Unit
): HttpClient {
    val client = HttpClient(httpClientEngine()) {

        defaultRequest {
            url(baseUrl)
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }

        install(Logging) {
            level = LogLevel.ALL
        }

        expectSuccess = true // essentiel : tout statut hors 2xx (200-299) déclenche une exception


        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.value == 401) {
                    CoroutineScope(Dispatchers.Default).launch { onUnauthorized() }
                }
            }

            handleResponseExceptionWithRequest { exception, _ ->
                val clientException = exception as? io.ktor.client.plugins.ResponseException ?: return@handleResponseExceptionWithRequest
                val rawBody = try {
                    clientException.response.bodyAsText()
                } catch (_: Exception) {
                    ""
                }


                val json = Json { ignoreUnknownKeys = true }

                // Essaie d'abord le format applicatif standard
                val appError = try {
                    json.decodeFromString<ApiErrorResponse>(rawBody)
                } catch (_: Exception) {
                    null
                }

                if (appError != null) {
                    throw ApiException(appError.error.codeAsString, appError.error.message, appError.error.details)
                }

                // Sinon, tente le format LexikJWT natif : {"code": 401, "message": "..."}
                val jwtError = try {
                    json.decodeFromString<LexikJwtErrorDto>(rawBody)
                } catch (e: Exception) {
                    null
                }

                throw ApiException(
                    code = jwtError?.code?.toString() ?: "UNKNOWN",
                    message = jwtError?.message ?: "Une erreur est survenue",
                    details = emptyList()
                )
            }
        }

    }

    client.plugin(HttpSend).intercept { request ->
        val token = tokenStorage.getToken()
        if (token != null) {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
        execute(request)
    }

    return client
}

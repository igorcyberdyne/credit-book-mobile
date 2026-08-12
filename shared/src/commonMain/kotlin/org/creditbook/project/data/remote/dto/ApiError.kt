package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class ApiErrorData(
    val code: JsonElement,
    val message: String,
    val details: List<String> = emptyList()
) {
    // Normalise le code en String quel que soit son type JSON d'origine (entier ou chaîne)
    val codeAsString: String
        get() = (code as? JsonPrimitive)?.let {
            if (it.isString) it.content else it.content
        } ?: "UNKNOWN"
}

@Serializable
data class ApiErrorResponse(
    val error: ApiErrorData
)

@Serializable
data class LexikJwtErrorDto(
    val code: Int,
    val message: String
)

class ApiException(
    val code: String,
    override val message: String,
    val details: List<String> = emptyList()
) : Exception(message) {
    fun isBusinessException(): Boolean {
        return code == "###"
    }
}
package org.creditbook.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginCommand(
    val email: String,
    val password: String
)
@Serializable
data class RefreshTokenCommand(
    @SerialName("refresh_token")
    val refreshToken: String
)

@Serializable
data class UserDto(
    val uuid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String>
)

@Serializable
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserDto,
    val shop: ShopDto,
)

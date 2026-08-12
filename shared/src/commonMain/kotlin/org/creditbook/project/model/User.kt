package org.creditbook.project.model

import org.creditbook.project.data.remote.dto.UserDto

data class User(
    val uuid: String,
    val email: String,
    val firstName: String,
    val lastName: String?,
    val phone: String?,
    val roles: List<String>
) {
    val isManager: Boolean get() = "ROLE_MANAGER" in roles
    val displayName: String get() = "$firstName ${lastName ?: ""}"
    val roleName: String get() = if (isManager) "Gérant" else "Employé"
}

fun UserDto.toDomain(): User = User(uuid, email, firstName, lastName, phone, roles)
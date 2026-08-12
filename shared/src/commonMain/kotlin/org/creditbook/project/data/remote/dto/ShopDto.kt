package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShopDto(
    val uuid: String,
    val name: String,
    val address: String?,
    val postalCode: String?,
    val city: String?,
    val country: String?,
    val phone: String?,
    val currency: String?
)

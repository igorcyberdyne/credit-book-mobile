package org.creditbook.project.model

data class Shop(
    val uuid: String,
    val name: String,
    val address: String?,
    val postalCode: String?,
    val city: String?,
    val country: String?,
    val phone: String?,
    val currency: String
)

data class Session(
    val user: User,
    val shop: Shop
)
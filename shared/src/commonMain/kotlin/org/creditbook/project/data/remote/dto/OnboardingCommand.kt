package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingCommand(
    val shopName: String,
    val address: String? = null,
    val postalCode: String? = null,
    val city: String? = null,
    val country: String,
    val shopPhone: String? = null,
    val currency: String? = null,
    val timezone: String? = null,
    val firstname: String,
    val lastname: String? = null,
    val email: String,
    val phone: String? = null,
    val password: String
)
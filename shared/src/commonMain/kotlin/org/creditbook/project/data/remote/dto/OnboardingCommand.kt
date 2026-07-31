package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingCommand(
    val shopName: String,
    val address: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val shopPhone: String,
    val currency: String? = null,
    val timezone: String? = null,
    val firstname: String,
    val lastname: String? = null,
    val email: String,
    val phone: String,
    val password: String
)
package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ShopDto(
    val name: String,
    val address: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val currency: String
)

@Serializable
data class DashboardStatsDto(
    val customers: Int,
    val customersWithDebt: Int,
    val ledgerEntries: Int,
    val debts: Int,
    val payments: Int,
    val totalDebtInCents: Long,
    val todayDebtInCents: Long,
    val todayPaymentsInCents: Long,
    val shop: ShopDto
)
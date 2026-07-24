package org.creditbook.project.model

import org.creditbook.project.data.remote.dto.DashboardStatsDto

data class DashboardStats(
    val shopName: String,
    val address: String,
    val postalCode: String,
    val city: String,
    val country: String,
    val currency: String,

    val totalDue: Money,
    val customersWithDebtCount: Int
)

fun DashboardStatsDto.toDomain(): DashboardStats = DashboardStats(
    shopName = shop.name,
    address = shop.address,
    postalCode = shop.postalCode,
    city = shop.city,
    country = shop.country,
    currency = shop.currency,
    totalDue = Money.fromCents(totalDebtInCents),
    customersWithDebtCount = customersWithDebt
)
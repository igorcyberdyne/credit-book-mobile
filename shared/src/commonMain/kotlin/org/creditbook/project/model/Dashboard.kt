package org.creditbook.project.model

import org.creditbook.project.data.remote.dto.DashboardStatsDto

data class DashboardStats(
    val totalDue: Money?, // null si donnée non disponible hors-ligne
    val customersWithDebtCount: Int?,
    val isOffline: Boolean = false
)

fun DashboardStatsDto.toDomain(): DashboardStats = DashboardStats(
    totalDue = Money.fromCents(totalDebtInCents),
    customersWithDebtCount = customersWithDebt,
    isOffline = false
)
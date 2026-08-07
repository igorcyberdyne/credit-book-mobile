package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStatsDto(
    val customers: Int,
    val customersWithDebt: Int,
    val ledgerEntries: Int,
    val debts: Int,
    val payments: Int,
    val totalDebtInCents: Long,
    val todayDebtInCents: Long,
    val todayPaymentsInCents: Long
)
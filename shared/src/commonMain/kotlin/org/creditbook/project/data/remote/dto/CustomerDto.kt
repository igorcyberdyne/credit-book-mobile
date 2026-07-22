package org.creditbook.project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BalanceDto(
    val balanceInCents: Long,
    val totalDebtInCents: Long,
    val totalPaidInCents: Long,
    val operations: Int
)

@Serializable
data class CustomerDto(
    val uuid: String,
    val firstname: String,
    val lastname: String? = null,
    val phone: String? = null,
    val note: String? = null,
    val balance: BalanceDto? = null
)

@Serializable
data class PaginationDto(
    val nextUri: String? = null,
    val previousUri: String? = null,
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)

@Serializable
data class CustomersPageDto(
    val customers: List<CustomerDto>,
    val pagination: PaginationDto
)

@Serializable
data class CreateCustomerCommand(
    val firstname: String,
    val lastname: String? = null,
    val phone: String? = null,
    val note: String? = null
)
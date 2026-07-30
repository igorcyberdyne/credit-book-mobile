package org.creditbook.project.model

import org.creditbook.project.data.remote.dto.BalanceDto
import org.creditbook.project.data.remote.dto.CustomerDto
import org.creditbook.project.data.remote.dto.CustomersPageDto

data class CustomersPage(
    val customers: List<Customer>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val hasNextPage: Boolean
)

fun CustomersPageDto.toDomain(): CustomersPage = CustomersPage(
    customers = customers.map { it.toDomain() },
    currentPage = pagination.page,
    totalPages = pagination.pages,
    totalItems = pagination.total,
    hasNextPage = !pagination.nextUri.isNullOrEmpty()
)

data class CustomerBalance(
    val balance: Money,
    val totalDebt: Money,
    val totalPaid: Money,
    val operationsCount: Int,
    val lastDate: String? = null
) {
    val hasDebt: Boolean get() = balance.isPositive()
}

data class Customer(
    val uuid: String,
    val firstname: String,
    val lastname: String?,
    val phone: String?,
    val note: String?,
    val balance: CustomerBalance
) {
    val displayName: String
        get() = listOfNotNull(firstname, lastname).joinToString(" ")

    val initials: String
        get() = listOfNotNull(firstname.firstOrNull(), lastname?.firstOrNull())
            .joinToString("")
            .uppercase()
}

fun BalanceDto.toDomain(): CustomerBalance = CustomerBalance(
    balance = Money.fromCents(balanceInCents),
    totalDebt = Money.fromCents(totalDebtInCents),
    totalPaid = Money.fromCents(totalPaidInCents),
    operationsCount = operations,
    lastDate = lastDate,
)

fun CustomerDto.toDomain(): Customer = Customer(
    uuid = uuid,
    firstname = firstname,
    lastname = lastname,
    phone = phone,
    note = note,
    balance = balance?.toDomain() ?: CustomerBalance(
        balance = Money.fromCents(0),
        totalDebt = Money.fromCents(0),
        totalPaid = Money.fromCents(0),
        operationsCount = 0,
        lastDate = ""
    )
)
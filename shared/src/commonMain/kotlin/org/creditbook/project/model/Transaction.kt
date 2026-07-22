package org.creditbook.project.model

import org.creditbook.project.data.remote.dto.EntryType
import org.creditbook.project.data.remote.dto.PaginationDto
import org.creditbook.project.data.remote.dto.TransactionEntryDto
import org.creditbook.project.data.remote.dto.TransactionsPageDto

enum class TransactionType { DEBT, PAYMENT }

data class TransactionEntry(
    val uuid: String,
    val type: TransactionType,
    val amount: Money,
    val description: String?,
    val occurredAt: String?,
    val paymentMethod: String?
)

fun TransactionEntryDto.toDomain(): TransactionEntry = TransactionEntry(
    uuid = uuid,
    type = if (type == EntryType.DEBT) TransactionType.DEBT else TransactionType.PAYMENT,
    amount = Money.fromDecimal(amount),
    description = description,
    occurredAt = occurredAt,
    paymentMethod = paymentMethod?.name
)

data class TransactionsPage(
    val statistics: CustomerBalance,
    val entries: List<TransactionEntry>,
    val pagination: PaginationDto
)

fun TransactionsPageDto.toDomain(): TransactionsPage = TransactionsPage(
    statistics = statistics.toDomain(),
    entries = entries.map { it.toDomain() },
    pagination = pagination
)
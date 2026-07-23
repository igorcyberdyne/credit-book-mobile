package org.creditbook.project.model

import org.creditbook.project.data.remote.dto.EntryStatus
import org.creditbook.project.data.remote.dto.EntryType
import org.creditbook.project.data.remote.dto.PaginationDto
import org.creditbook.project.data.remote.dto.TransactionEntryDto
import org.creditbook.project.data.remote.dto.TransactionsPageDto

enum class TransactionType { DEBT, PAYMENT }
enum class TransactionStatus { ACTIVE, CANCELLED }

data class TransactionEntry(
    val uuid: String,
    val type: TransactionType,
    val amount: Money,
    val description: String?,
    val occurredAt: String?,
    val paymentMethod: String?,
    val status: TransactionStatus,
    val isCorrection: Boolean,
    val canReverse: Boolean,
    val canCorrect: Boolean,
    val icon: String?,
    val color: String?,
    val badge: String?
) {
    val isCancelled: Boolean get() = status == TransactionStatus.CANCELLED
}

fun TransactionEntryDto.toDomain(): TransactionEntry = TransactionEntry(
    uuid = uuid,
    type = if (type == EntryType.DEBT) TransactionType.DEBT else TransactionType.PAYMENT,
    amount = Money.fromDecimal(amount),
    description = description,
    occurredAt = occurredAt,
    paymentMethod = paymentMethod?.name,
    status = if (status == EntryStatus.CANCELLED) TransactionStatus.CANCELLED else TransactionStatus.ACTIVE,
    isCorrection = isCorrection,
    canReverse = canReverse,
    canCorrect = canCorrect,
    icon = icon,
    color = color,
    badge = badge
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
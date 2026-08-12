package org.creditbook.project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val uuid: String,
    val amountInCents: Long,
    val label: String,
    val createdAt: String
)

@Serializable
data class CreateTransactionCommand(
    val amountInCents: Long,
    val label: String,
    val localUuid: String,
    val createdAt: String
)

@Serializable
enum class EntryType {
    @SerialName("DEBT") DEBT,
    @SerialName("PAYMENT") PAYMENT
}

@Serializable
enum class PaymentMethod {
    @SerialName("CASH") CASH,
    @SerialName("CARD") CARD
}

@Serializable
enum class EntryStatus {
    @SerialName("ACTIVE") ACTIVE,
    @SerialName("CANCELLED") CANCELLED
}

@Serializable
data class TransactionEntryDto(
    val uuid: String,
    val type: EntryType,
    val amount: String, // décimal en chaîne, ex. "10.00"
    val description: String? = null,
    val occurredAt: String? = null, // absent parfois, cf. remarque
    val paymentMethod: PaymentMethod? = null,
    val status: EntryStatus = EntryStatus.ACTIVE,
    val isCorrection: Boolean = false,
    val canReverse: Boolean = true,
    val canCorrect: Boolean = true,
    val icon: String? = null,
    val color: String? = null,
    val badge: String? = null
)

@Serializable
data class TransactionsPageDto(
    val statistics: BalanceDto,       // même forme que le "balance" du client
    val entries: List<TransactionEntryDto>,
    val pagination: PaginationDto
)

interface TransactionCommand;

@Serializable
data class CreateDebtCommand(
    val amountInCents: Long,
    val description: String? = null,
    val occurredAt: String? = null
): TransactionCommand

@Serializable
data class CreatePaymentCommand(
    val amountInCents: Long,
    val paymentMethod: String,
    val description: String? = null,
    val occurredAt: String? = null
): TransactionCommand

@Serializable
data class CorrectEntryCommand(
    val amountInCents: Long,
    val description: String? = null,
    val paymentMethod: String? = null,
    val occurredAt: String? = null
)

@Serializable
data class CancelEntryCommand(
    val reason: String? = null
)
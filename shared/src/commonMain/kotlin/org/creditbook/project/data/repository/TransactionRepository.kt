package org.creditbook.project.data.repository

import com.benasher44.uuid.uuid4
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.descriptors.StructureKind
import org.creditbook.project.data.local.SessionDatabase
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.remote.dto.ApiResponse
import org.creditbook.project.data.remote.dto.CancelEntryCommand
import org.creditbook.project.data.remote.dto.CorrectEntryCommand
import org.creditbook.project.data.remote.dto.CreateDebtCommand
import org.creditbook.project.data.remote.dto.CreatePaymentCommand
import org.creditbook.project.data.remote.dto.TransactionCommand
import org.creditbook.project.data.remote.dto.TransactionEntryDto
import org.creditbook.project.data.remote.dto.TransactionsPageDto
import org.creditbook.project.model.Money
import org.creditbook.project.model.TransactionType
import org.creditbook.project.model.TransactionsPage
import org.creditbook.project.model.toDomain
import org.creditbook.project.shared.db.AppDatabase
import org.creditbook.project.sync.ConnectivityObserver
import org.creditbook.project.ui.common.error.ErrorDialogState

class TransactionRepository(
    private val httpClient: HttpClient,
    private val connectivityObserver: ConnectivityObserver,
    private val database: AppDatabase,
    private val sessionDatabase: SessionDatabase
) {
    private val shopUuid: String
        get() {
            val cachedSession = sessionDatabase.getCachedSession()

            return cachedSession.shop.uuid
        }


    suspend fun addDebt(customerUuid: String, command: CreateDebtCommand) {
        val localUuid: String = uuid4().toString()

        database.transactionQueries.insertEntry(
            localUuid = localUuid,
            customerUuid = customerUuid,
            type = TransactionType.DEBT.name,
            amountInCents = command.amountInCents,
            description = command.description,
            paymentMethod = null,
            occurredAt = command.occurredAt,
            status = "pending",
            syncAttempts = 0,
            shopUuid = shopUuid,
        )

        val online = connectivityObserver.isOnline()

        if (online) syncPendingTransactions(true, localUuid)
    }

    suspend fun addPayment(
        customerUuid: String,
        command: CreatePaymentCommand
    ) {
        val localUuid: String = uuid4().toString()

        database.transactionQueries.insertEntry(
            localUuid = localUuid,
            customerUuid = customerUuid,
            type = TransactionType.PAYMENT.name,
            amountInCents = command.amountInCents,
            description = command.description,
            paymentMethod = command.paymentMethod,
            occurredAt = command.occurredAt,
            status = "pending",
            syncAttempts = 0,
            shopUuid
        )

        val online = connectivityObserver.isOnline()

        if (online) syncPendingTransactions(true, localUuid)
    }

    // Route corrigée : /ledgers/{uuid}/correct
    suspend fun correctEntry(
        entryUuid: String,
        command: CorrectEntryCommand
    ) {
        httpClient.post("/api/ledgers/$entryUuid/correct") {
            contentType(ContentType.Application.Json)
            setBody(command)
        }
    }

    // Route corrigée : /ledgers/{uuid}/reverse (plutôt que /cancel)
    suspend fun cancelEntry(entryUuid: String, reason: String? = null) {
        httpClient.post("/api/ledgers/$entryUuid/reverse") {
            contentType(ContentType.Application.Json)
            setBody(CancelEntryCommand(reason))
        }
    }

    suspend fun fetchTransactionsForCustomer(customerUuid: String): TransactionsPage {
        return httpClient.get("/api/ledgers/customers/$customerUuid/ledger")
            .body<ApiResponse<TransactionsPageDto>>().data.toDomain()
    }

    suspend fun syncPendingTransactions(isUserAction: Boolean = false, localUuid: String? = null) {
        val pending = database.transactionQueries.selectPending(shopUuid).executeAsList()

        for (entry in pending) {
            try {
                var resource: String
                var command: TransactionCommand

                if (entry.type == "DEBT") {
                    resource = "debts"
                    command = CreateDebtCommand(
                        amountInCents = entry.amountInCents,
                        description = entry.description,
                        occurredAt = entry.occurredAt
                    )
                } else {
                    resource = "payments"
                    command = CreatePaymentCommand(
                        amountInCents = entry.amountInCents,
                        paymentMethod = entry.paymentMethod ?: "CASH",
                        description = entry.description,
                        occurredAt = entry.occurredAt
                    )
                }

                val response = httpClient.post("/api/ledgers/customers/${entry.customerUuid}/$resource") {
                    contentType(ContentType.Application.Json)
                    setBody(command)
                }.body<ApiResponse<TransactionEntryDto>>().data

                database.transactionQueries.markSynced(response.uuid, entry.id)
            } catch (e: ApiException) {
                if (isUserAction && localUuid != null && entry.localUuid == localUuid) {
                    database.transactionQueries.delete(localUuid)

                    if (e.code != "###") {
                        throw ApiException(
                            code = e.code,
                            message = "Impossible d’enregistrer l’opération pour le moment. Veuillez réessayer ultérieurement.",
                            details = e.details
                        )
                    }

                    throw e
                }
            } catch (_: Exception) {
                database.transactionQueries.incrementSyncAttempts(entry.id)
            }
        }
    }
}
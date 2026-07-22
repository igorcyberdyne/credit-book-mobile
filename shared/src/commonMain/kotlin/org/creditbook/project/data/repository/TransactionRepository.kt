package org.creditbook.project.data.repository

import com.benasher44.uuid.uuid4
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.creditbook.project.data.remote.dto.ApiResponse
import org.creditbook.project.data.remote.dto.CancelEntryCommand
import org.creditbook.project.data.remote.dto.CorrectEntryCommand
import org.creditbook.project.data.remote.dto.CreateDebtCommand
import org.creditbook.project.data.remote.dto.CreatePaymentCommand
import org.creditbook.project.data.remote.dto.TransactionEntryDto
import org.creditbook.project.data.remote.dto.TransactionsPageDto
import org.creditbook.project.model.Money
import org.creditbook.project.model.TransactionType
import org.creditbook.project.model.TransactionsPage
import org.creditbook.project.model.toDomain
import org.creditbook.project.shared.db.AppDatabase
import org.creditbook.project.sync.ConnectivityObserver

class TransactionRepository(
    private val httpClient: HttpClient,
    private val database: AppDatabase,
    private val connectivityObserver: ConnectivityObserver
) {

    suspend fun addDebt(customerUuid: String, amount: Money, description: String? = null) {
        database.transactionQueries.insertEntry(
            localUuid = uuid4().toString(),
            customerUuid = customerUuid,
            type = TransactionType.DEBT.name,
            amountInCents = amount.cents(),
            description = description,
            paymentMethod = null,
            occurredAt = null,
            status = "pending",
            syncAttempts = 0
        )

        val allPending = database.transactionQueries.selectPending().executeAsList()
        println("Après insertEntry, nombre de transactions pending: ${allPending.size}")
        allPending.forEach { println("  -> ${it.localUuid} / ${it.amountInCents} centimes / status=${it.status}") }

        val online = connectivityObserver.isOnline()

        if (online) syncPendingTransactions()
    }

    suspend fun addPayment(
        customerUuid: String,
        amount: Money,
        paymentMethod: String,
        description: String? = null
    ) {
        database.transactionQueries.insertEntry(
            localUuid = uuid4().toString(),
            customerUuid = customerUuid,
            type = TransactionType.PAYMENT.name,
            amountInCents = amount.cents(),
            description = description,
            paymentMethod = paymentMethod,
            occurredAt = null,
            status = "pending",
            syncAttempts = 0
        )
        if (connectivityObserver.isOnline()) syncPendingTransactions()
    }

    // Route corrigée : /ledgers/{uuid}/correct
    suspend fun correctEntry(
        entryUuid: String,
        amountInCents: Long,
        description: String? = null,
        paymentMethod: String? = null
    ) {
        httpClient.post("/api/ledgers/$entryUuid/correct") {
            contentType(ContentType.Application.Json)
            setBody(CorrectEntryCommand(amountInCents, description, paymentMethod))
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

    suspend fun syncPendingTransactions() {
        val pending = database.transactionQueries.selectPending().executeAsList()

        for (entry in pending) {
            try {
                // Routes corrigées : /ledgers/customers/{uuid}/debts et /payments
                val response = if (entry.type == "DEBT") {
                    httpClient.post("/api/ledgers/customers/${entry.customerUuid}/debts") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            CreateDebtCommand(
                                amountInCents = entry.amountInCents,
                                description = entry.description,
                                occurredAt = null
                            )
                        )
                    }
                } else {
                    httpClient.post("/api/ledgers/customers/${entry.customerUuid}/payments") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            CreatePaymentCommand(
                                amountInCents = entry.amountInCents,
                                paymentMethod = entry.paymentMethod ?: "CASH",
                                description = entry.description,
                                occurredAt = null
                            )
                        )
                    }
                }.body<ApiResponse<TransactionEntryDto>>().data

                database.transactionQueries.markSynced(response.uuid, entry.id)
            } catch (e: Exception) {
                println("Échec de sync pour ${entry.localUuid}: ${e.message}")
                database.transactionQueries.incrementSyncAttempts(entry.id)
            }
        }
    }
}
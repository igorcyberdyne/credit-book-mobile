package org.creditbook.project.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.creditbook.project.data.local.SessionDatabase
import org.creditbook.project.data.repository.TransactionRepository

class SyncQueue(
    private val connectivityObserver: ConnectivityObserver,
    private val transactionRepository: TransactionRepository,
    private val sessionDatabase: SessionDatabase
) {
    fun startObserving(scope: CoroutineScope) {
        scope.launch {
            connectivityObserver.observe().collect { isOnline ->
                if (isOnline && sessionDatabase.hasSession()) {
                    transactionRepository.syncPendingTransactions()
                }
            }
        }
    }
}
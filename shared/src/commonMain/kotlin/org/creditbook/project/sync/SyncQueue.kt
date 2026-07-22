package org.creditbook.project.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.creditbook.project.data.repository.TransactionRepository

class SyncQueue(
    private val connectivityObserver: ConnectivityObserver,
    private val transactionRepository: TransactionRepository
) {
    fun startObserving(scope: CoroutineScope) {
        scope.launch {
            connectivityObserver.observe().collect { isOnline ->
                if (isOnline) {
                    transactionRepository.syncPendingTransactions()
                }
            }
        }
    }
}
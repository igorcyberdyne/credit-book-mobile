package org.creditbook.project.sync

import kotlinx.coroutines.flow.Flow

expect class ConnectivityObserver {
    fun isOnline(): Boolean
    fun observe(): Flow<Boolean>
}
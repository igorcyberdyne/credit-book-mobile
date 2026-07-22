package org.creditbook.project.di

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object AuthEvents {
    private val _onUnauthorized = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val onUnauthorized: SharedFlow<Unit> = _onUnauthorized

    suspend fun emitUnauthorized() {
        _onUnauthorized.emit(Unit)
    }
}
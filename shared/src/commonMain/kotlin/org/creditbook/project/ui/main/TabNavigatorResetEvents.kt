package org.creditbook.project.ui.main

import cafe.adriel.voyager.navigator.tab.Tab
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object TabNavigatorResetEvents {
    private val _resetRequests = MutableSharedFlow<Tab>(extraBufferCapacity = 1)
    val resetRequests: SharedFlow<Tab> = _resetRequests

    suspend fun requestReset(tab: Tab) {
        _resetRequests.emit(tab)
    }
}
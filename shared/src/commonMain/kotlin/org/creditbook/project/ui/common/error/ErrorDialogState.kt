package org.creditbook.project.ui.common.error

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ErrorDialogState {
    var message by mutableStateOf<String?>(null)
        private set

    fun show(message: String) {
        this.message = message
    }

    fun dismiss() {
        message = null
    }
}
package org.creditbook.project.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.creditbook.project.data.remote.dto.CorrectEntryCommand
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.lib.formatDate
import org.creditbook.project.lib.fromString
import org.creditbook.project.model.Money
import kotlin.time.ExperimentalTime

data class CorrectEntryUiState(
    val amountText: String,
    val description: String,
    val occurredAt: LocalDateTime? = null,
    val paymentMethod: String?, // null si c'est une dette, "CASH"/"CARD" si paiement
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false
) {
    val isAmountValid: Boolean
        get() = amountText.toDoubleOrNull()?.let { it > 0 } ?: false
}

class CorrectEntryViewModel(
    private val entryUuid: String,
    initialAmount: Money,
    initialDescription: String?,
    initialOccurredAt: LocalDateTime?,
    initialPaymentMethod: String?,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        CorrectEntryUiState(
            amountText = initialAmount.decimal(),
            description = initialDescription ?: "",
            occurredAt = initialOccurredAt,
            paymentMethod = initialPaymentMethod
        )
    )
    val state: StateFlow<CorrectEntryUiState> = _state

    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        _state.update { it.copy(amountText = filtered, error = null) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onOccurredAtChange(dateTime: LocalDateTime?) {
        _state.update { it.copy(occurredAt = dateTime) }
    }

    @OptIn(ExperimentalTime::class)
    fun submit() {
        val current = _state.value
        if (!current.isAmountValid) {
            _state.update { it.copy(error = "Montant invalide") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            try {
                val occurredAt = current.occurredAt?.toInstant(TimeZone.currentSystemDefault())?.toString()

                transactionRepository.correctEntry(
                    entryUuid = entryUuid,
                    command = CorrectEntryCommand(
                        amountInCents = Money.fromDecimal(current.amountText).cents(),
                        description = current.description.ifBlank { null },
                        occurredAt = occurredAt,
                        paymentMethod = current.paymentMethod?.ifBlank { null },
                    )
                )
                _state.update { it.copy(isSubmitting = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, error = e.message ?: "Erreur inattendue") }
            }
        }
    }
}
package org.creditbook.project.ui.transactions.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.remote.dto.CreateDebtCommand
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.lib.formatDate
import org.creditbook.project.model.Money
import org.creditbook.project.ui.common.error.ErrorDialogState
import kotlin.text.ifEmpty
import kotlin.time.ExperimentalTime

data class AddDebtUiState(
    val amountText: String = "",
    val description: String = "",
    val occurredAt: LocalDateTime? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false
) {
    val isAmountValid: Boolean
        get() = amountText.toDoubleOrNull()?.let { it > 0 } ?: false
}

class AddDebtViewModel(
    private val clientUuid: String,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddDebtUiState())
    val state: StateFlow<AddDebtUiState> = _state

    fun onAmountChange(value: String) {
        // N'accepte que les chiffres et un séparateur décimal
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
                val amount = Money.fromDecimal(current.amountText)
                val occurredAt = current.occurredAt?.toInstant(TimeZone.currentSystemDefault())?.toString()

                transactionRepository.addDebt(
                    customerUuid = clientUuid,
                    command = CreateDebtCommand(
                        amountInCents = amount.cents(),
                        description = current.description.ifBlank { null },
                        occurredAt = occurredAt
                    )
                )
                _state.update { it.copy(isSubmitting = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false) }
                ErrorDialogState.show(e.message ?: "Erreur inattendue")
            }
        }
    }
}
package org.creditbook.project.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.model.Money

data class CorrectEntryUiState(
    val amountText: String,
    val description: String,
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
    initialPaymentMethod: String?,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        CorrectEntryUiState(
            amountText = initialAmount.decimal(),
            description = initialDescription ?: "",
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

    fun submit() {
        val current = _state.value
        if (!current.isAmountValid) {
            _state.update { it.copy(error = "Montant invalide") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            try {
                transactionRepository.correctEntry(
                    entryUuid = entryUuid,
                    amountInCents = Money.fromDecimal(current.amountText).cents(),
                    description = current.description.ifBlank { null },
                    paymentMethod = current.paymentMethod?.ifBlank { null },
                )
                _state.update { it.copy(isSubmitting = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, error = e.message ?: "Erreur inattendue") }
            }
        }
    }
}
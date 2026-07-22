package org.creditbook.project.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.model.Money

data class AddDebtUiState(
    val amountText: String = "",
    val description: String = "",
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

    fun submit() {
        println("submit() appelé, montant=${_state.value.amountText}, valide=${_state.value.isAmountValid}")

        val current = _state.value
        if (!current.isAmountValid) {
            _state.update { it.copy(error = "Montant invalide") }
            return
        }

        viewModelScope.launch {
            println("Entrée dans le coroutine de submit")
            _state.update { it.copy(isSubmitting = true, error = null) }
            try {
                val amount = Money.fromDecimal(current.amountText)
                transactionRepository.addDebt(
                    customerUuid = clientUuid,
                    amount = amount,
                    description = current.description.ifBlank { null }
                )
                _state.update { it.copy(isSubmitting = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, error = e.message ?: "Erreur inattendue") }
            }
        }
    }
}
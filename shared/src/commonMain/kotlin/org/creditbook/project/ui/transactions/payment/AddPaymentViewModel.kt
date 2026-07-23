package org.creditbook.project.ui.transactions.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.repository.CustomerRepository
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.model.Customer
import org.creditbook.project.model.Money
import org.creditbook.project.sync.ConnectivityObserver

enum class PaymentMethodOption { CASH, CARD }

data class AddPaymentUiState(
    val currentBalance: Money? = null,
    val isOffline: Boolean = false,
    val amountText: String = "",
    val description: String = "",
    val paymentMethod: PaymentMethodOption = PaymentMethodOption.CASH,
    val isLoadingCustomer: Boolean = true,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false,
    val customer: Customer? = null
) {
    val isAmountValid: Boolean
        get() = amountText.toDoubleOrNull()?.let { it > 0 } ?: false

    val willFullySettleDebt: Boolean
        get() = currentBalance != null &&
                amountText.toDoubleOrNull()?.let {
                    Money.fromDecimal(amountText).cents() >= currentBalance.cents()
                } ?: false

    // Solde connu mais commerçant hors-ligne : les raccourcis ne peuvent pas être fiables
    val showOfflineBalanceWarning: Boolean
        get() = isOffline && currentBalance == null && !isLoadingCustomer
}

class AddPaymentViewModel(
    private val customerUuid: String,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(AddPaymentUiState())
    val state: StateFlow<AddPaymentUiState> = _state

    init {
        loadCustomerBalance()
    }

    private fun loadCustomerBalance() {
        viewModelScope.launch {
            val isOnline = connectivityObserver.isOnline()

            if (!isOnline) {
                _state.update { it.copy(isOffline = true, isLoadingCustomer = false) }
                return@launch
            }

            try {
                val customer = customerRepository.fetchCustomer(customerUuid)
                _state.update {
                    it.copy(
                        currentBalance = customer.balance.balance,
                        isOffline = false,
                        isLoadingCustomer = false,
                        customer = customer
                    )
                }
            } catch (e: Exception) {
                // Échec réseau malgré isOnline() = true (ex. coupure au moment précis de l'appel)
                _state.update { it.copy(isOffline = true, isLoadingCustomer = false, error = null) }
            }
        }
    }

    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')
        _state.update { it.copy(amountText = filtered, error = null) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onPaymentMethodChange(method: PaymentMethodOption) {
        _state.update { it.copy(paymentMethod = method) }
    }

    fun payFull() {
        state.value.currentBalance?.let {
            _state.update { s -> s.copy(amountText = it.decimal(), error = null) }
        }
    }

    fun payHalf() {
        state.value.currentBalance?.let {
            val half = Money.fromCents(it.cents() / 2)
            _state.update { s -> s.copy(amountText = half.decimal(), error = null) }
        }
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
                transactionRepository.addPayment(
                    customerUuid = customerUuid,
                    amount = Money.fromDecimal(current.amountText),
                    paymentMethod = current.paymentMethod.name,
                    description = current.description.ifBlank { null }
                )
                _state.update { it.copy(isSubmitting = false, isSubmitted = true) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        error = e.message ?: "Erreur inattendue"
                    )
                }
            }
        }
    }
}
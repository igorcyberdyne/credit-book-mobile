package org.creditbook.project.ui.customers.detail


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.repository.CustomerRepository
import org.creditbook.project.data.repository.TransactionRepository
import org.creditbook.project.model.Customer
import org.creditbook.project.model.TransactionEntry

data class CustomerDetailUiState(
    val customer: Customer? = null,
    val transactions: List<TransactionEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class CustomerDetailViewModel(
    private val customerUuid: String,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerDetailUiState())
    val state: StateFlow<CustomerDetailUiState> = _state

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val customer = customerRepository.fetchCustomer(customerUuid)
                val page = transactionRepository.fetchTransactionsForCustomer(customerUuid)
                _state.update {
                    it.copy(
                        customer = customer,
                        transactions = page.entries,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Erreur inconnue") }
            }
        }
    }

    fun cancelEntry(entryUuid: String, reason: String? = null) {
        viewModelScope.launch {
            try {
                transactionRepository.cancelEntry(entryUuid, reason)
                loadAll() // recharge pour refléter le nouveau solde et le statut annulé
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
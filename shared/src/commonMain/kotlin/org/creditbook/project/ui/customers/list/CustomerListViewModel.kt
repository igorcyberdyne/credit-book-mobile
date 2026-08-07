package org.creditbook.project.ui.customers.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.local.SessionDatabase
import org.creditbook.project.data.repository.CustomerRepository
import org.creditbook.project.model.Customer
import org.creditbook.project.model.DashboardStats
import org.creditbook.project.model.Session


data class CustomerListUiState(
    val customers: List<Customer> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = true,
    val error: String? = null,

    val stats: DashboardStats? = null,
    val searchQuery: String = "",
    val session: Session? = null,
)

class CustomerListViewModel(
    private val repository: CustomerRepository,
    private val sessionDatabase: SessionDatabase
) : ViewModel() {

    private val _state = MutableStateFlow(CustomerListUiState())
    val state: StateFlow<CustomerListUiState> = _state

    init {
        _state.update { it.copy(session = sessionDatabase.getCachedSession()) }
    }

    fun loadFirstPage() {
        val current = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val page = repository.fetchCustomers(page = 1, search = current.searchQuery)
                _state.update {
                    it.copy(
                        customers = page.customers,
                        currentPage = page.currentPage,
                        hasNextPage = page.hasNextPage,
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Erreur inconnue") }
            }
        }
    }

    fun loadNextPage() {
        val current = _state.value
        if (current.isLoadingMore || !current.hasNextPage) return

        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            try {
                val page = repository.fetchCustomers(page = current.currentPage + 1)
                _state.update {
                    it.copy(
                        customers = it.customers + page.customers,
                        currentPage = page.currentPage,
                        hasNextPage = page.hasNextPage,
                        isLoadingMore = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingMore = false, error = e.message) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        loadFirstPage() // relance la recherche à chaque frappe (à débouncer si besoin, cf. remarque plus bas)
    }

    fun refresh() {
        loadFirstPage()
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            try {
                val stats = repository.fetchDashboardStats()
                _state.update { it.copy(stats = stats) }
            } catch (_: Exception) {
                // Silencieux volontairement : l'absence de stats ne doit pas bloquer la liste
                // sans les chiffres (total dû, ardoises ouvertes) qui nécessitent le serveur.
                _state.update {
                    it.copy(
                        stats = DashboardStats(
                            totalDue = null,
                            customersWithDebtCount = null,
                            isOffline = true
                        )
                    )
                }
            }
        }
    }
}
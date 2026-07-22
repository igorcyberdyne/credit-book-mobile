package org.creditbook.project.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.remote.dto.CreateCustomerCommand
import org.creditbook.project.data.repository.CustomerRepository

data class NewCustomerUiState(
    val firstname: String = "",
    val lastname: String = "",
    val phone: String = "",
    val note: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val fieldErrors: List<String> = emptyList(),
    val isSubmitted: Boolean = false
) {
    val isValid: Boolean
        get() = firstname.isNotBlank()
}

class NewCustomerViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewCustomerUiState())
    val state: StateFlow<NewCustomerUiState> = _state

    fun onFirstnameChange(value: String) {
        _state.update { it.copy(firstname = value, error = null) }
    }

    fun onLastnameChange(value: String) {
        _state.update { it.copy(lastname = value) }
    }

    fun onPhoneChange(value: String) {
        _state.update { it.copy(phone = value) }
    }

    fun onNoteChange(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun submit() {
        val current = _state.value
        if (!current.isValid) {
            _state.update { it.copy(error = "Le prénom est obligatoire") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null, fieldErrors = emptyList()) }
            try {
                customerRepository.createCustomer(
                    CreateCustomerCommand(
                        firstname = current.firstname.trim(),
                        lastname = current.lastname.trim().ifBlank { null },
                        phone = current.phone.trim().ifBlank { null },
                        note = current.note.trim().ifBlank { null }
                    )
                )
                _state.update { it.copy(isSubmitting = false, isSubmitted = true) }
            } catch (e: ApiException) {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        error = if (e.details.isEmpty()) e.message else null,
                        fieldErrors = e.details
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        error = "Impossible de créer le client, vérifiez votre connexion"
                    )
                }
            }
        }
    }
}
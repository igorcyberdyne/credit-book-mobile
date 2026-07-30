package org.creditbook.project.ui.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.remote.dto.UpdateCustomerCommand
import org.creditbook.project.data.repository.CustomerRepository

data class EditCustomerUiState(
    val firstname: String,
    val lastname: String,
    val phone: String,
    val note: String,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val fieldErrors: List<String> = emptyList(),
    val isSubmitted: Boolean = false
) {
    val isValid: Boolean
        get() = firstname.isNotBlank() && phone.isNotBlank()
}

class EditCustomerViewModel(
    private val clientUuid: String,
    initialFirstname: String,
    initialLastname: String?,
    initialPhone: String?,
    initialNote: String?,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        EditCustomerUiState(
            firstname = initialFirstname,
            lastname = initialLastname ?: "",
            phone = initialPhone ?: "",
            note = initialNote ?: ""
        )
    )
    val state: StateFlow<EditCustomerUiState> = _state

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
            _state.update {
                it.copy(
                    error = when {
                        current.firstname.isBlank() -> "Le prénom est obligatoire"
                        current.phone.isBlank() -> "Le téléphone est obligatoire"
                        else -> null
                    }
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null, fieldErrors = emptyList()) }
            try {
                customerRepository.updateCustomer(
                    uuid = clientUuid,
                    UpdateCustomerCommand(
                        firstname = current.firstname.trim(),
                        lastname = current.lastname.trim().ifBlank { null },
                        phone = current.phone.trim(),
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
            } catch (_: Exception) {
                _state.update { it.copy(isSubmitting = false, error = "Impossible de modifier le client") }
            }
        }
    }
}
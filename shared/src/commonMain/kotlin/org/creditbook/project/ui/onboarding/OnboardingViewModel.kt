package org.creditbook.project.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.remote.dto.OnboardingCommand
import org.creditbook.project.data.repository.AuthRepository
import org.creditbook.project.ui.common.error.ErrorDialogState

enum class OnboardingStep { SHOP, USER }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.SHOP,

    // Shop
    val shopName: String = "",
    val address: String = "",
    val postalCode: String = "",
    val city: String = "",
    val country: String = "France",
    val shopPhone: String = "",
    val currency: String = "EURO",
    val timezone: String = TimeZone.currentSystemDefault().id,

    // User
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val passwordConfirm: String = "",

    val isSubmitting: Boolean = false,
    val error: String? = null,
    val fieldErrors: List<String> = emptyList(),
    val isCompleted: Boolean = false
) {
    val isShopStepValid: Boolean
        get() = shopName.isNotBlank() && address.isNotBlank() && postalCode.isNotBlank() &&
                city.isNotBlank() && country.isNotBlank() && shopPhone.isNotBlank()

    val isUserStepValid: Boolean
        get() = firstname.isNotBlank() && email.isNotBlank() && phone.isNotBlank() &&
                password.isNotBlank() && password == passwordConfirm && password.length >= 8
}

class OnboardingViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state

    fun onShopNameChange(v: String) = _state.update { it.copy(shopName = v, error = null) }
    fun onAddressChange(v: String) = _state.update { it.copy(address = v) }
    fun onPostalCodeChange(v: String) = _state.update { it.copy(postalCode = v) }
    fun onCityChange(v: String) = _state.update { it.copy(city = v) }
    fun onCountryChange(v: String) = _state.update { it.copy(country = v) }
    fun onShopPhoneChange(v: String) = _state.update { it.copy(shopPhone = v) }
    fun onCurrencyChange(v: String) = _state.update { it.copy(currency = v) }

    fun onFirstnameChange(v: String) = _state.update { it.copy(firstname = v, error = null) }
    fun onLastnameChange(v: String) = _state.update { it.copy(lastname = v) }
    fun onEmailChange(v: String) = _state.update { it.copy(email = v) }
    fun onPhoneChange(v: String) = _state.update { it.copy(phone = v) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v, error = null) }
    fun onPasswordConfirmChange(v: String) =
        _state.update { it.copy(passwordConfirm = v, error = null) }

    fun goToUserStep() {
        if (_state.value.isShopStepValid) {
            _state.update { it.copy(step = OnboardingStep.USER, error = null) }
        } else {
            _state.update { it.copy(error = "Merci de remplir tous les champs obligatoires") }
        }
    }

    fun goBackToShopStep() {
        _state.update { it.copy(step = OnboardingStep.SHOP, error = null) }
    }

    fun submit() {
        val current = _state.value

        if (!current.isUserStepValid) {
            _state.update {
                it.copy(
                    error = when {
                        it.password.length < 8 -> "Le mot de passe doit faire au moins 8 caractères"
                        it.password != it.passwordConfirm -> "Les mots de passe ne correspondent pas"
                        else -> "Merci de remplir tous les champs obligatoires"
                    }
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null, fieldErrors = emptyList()) }
            try {
                authRepository.onboard(
                    OnboardingCommand(
                        shopName = current.shopName.trim(),
                        address = current.address.trim().ifBlank { null },
                        postalCode = current.postalCode.trim().ifBlank { null },
                        city = current.city.trim().ifBlank { null },
                        country = current.country.trim(),
                        shopPhone = current.shopPhone.trim().ifBlank { null },
                        currency = current.currency.ifBlank { null },
                        timezone = current.timezone.ifBlank { null },
                        firstname = current.firstname.trim(),
                        lastname = current.lastname.trim().ifBlank { null },
                        email = current.email.trim(),
                        phone = current.phone.trim().ifBlank { null },
                        password = current.password
                    )
                )
                _state.update { it.copy(isSubmitting = false, isCompleted = true) }
            } catch (e: ApiException) {
                _state.update { it.copy(isSubmitting = false) }

                var message = ""
                if (e.isBusinessException()) {
                    message = e.message
                }
                ErrorDialogState.show(message.ifEmpty { "Une erreur s’est produite. Veuillez réessayer ultérieurement." })
            } catch (_: Exception) {
                _state.update { it.copy(isSubmitting = false) }
                ErrorDialogState.show("Une erreur s’est produite. Veuillez réessayer ultérieurement.")
            }
        }
    }
}
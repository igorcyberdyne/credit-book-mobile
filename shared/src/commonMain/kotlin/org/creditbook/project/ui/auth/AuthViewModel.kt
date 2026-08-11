package org.creditbook.project.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.creditbook.project.data.remote.dto.ApiException
import org.creditbook.project.data.repository.AuthRepository
import org.creditbook.project.model.User

sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data class Success(val user: User) : LoginState
    data class Error(val message: String) : LoginState
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { LoginState.Loading }
            try {
                val user = repository.login(email, password)
                _state.update { LoginState.Success(user) }
            } catch (e: ApiException) {
                _state.update { LoginState.Error(e.message) } // message exact du serveur, ex. "Identifiants invalides" manager@balto.fr
            } catch (_: Exception) {
                _state.update { LoginState.Error("Impossible de se connecter, vérifiez votre connexion") }
            }
        }
    }
}
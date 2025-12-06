package mx.edu.utng.foodmarket.ui.features.auth.vendor_login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class VendorLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado del Formulario
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    // Estado de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Evento de Navegación
    private val _loginEventChannel = Channel<String>()
    val loginEvent = _loginEventChannel.receiveAsFlow()

    // Funciones
    fun onEmailChange(text: String) { _email.value = text }
    fun onPasswordChange(text: String) { _password.value = text }
    fun togglePasswordVisibility() { _passwordVisible.value = !_passwordVisible.value }

    fun login() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = "Por favor completa todos los campos"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authRepository.login(_email.value, _password.value)

            result.onSuccess { rol ->
                if (rol == "VENDEDOR") {
                    _loginEventChannel.send(rol)
                } else {
                    _errorMessage.value = "Esta cuenta no es de vendedor. Usa el acceso de cliente."
                    authRepository.cerrarSesion() // Cerramos porque no es el rol correcto
                }
            }

            result.onFailure {
                _errorMessage.value = "Error: Verifica tus credenciales."
            }

            _isLoading.value = false
        }
    }
}
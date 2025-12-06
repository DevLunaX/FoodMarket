package mx.edu.utng.foodmarket.ui.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository // ¡Hilt inyecta esto automáticamente!
) : ViewModel() {

    // 1. ESTADO DE LA PANTALLA (StateFlow)
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    // 2. CANAL DE EVENTOS (Para navegación de un solo disparo)
    private val _loginEventChannel = Channel<String>()
    val loginEvent = _loginEventChannel.receiveAsFlow()

    // 3. FUNCIONES QUE LLAMA LA UI
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
            _errorMessage.value = null // Limpiar errores previos

            val result = authRepository.login(_email.value, _password.value)

            result.onSuccess { rol ->
                // Si es éxito, enviamos el evento de navegación
                _loginEventChannel.send(rol)
            }

            result.onFailure {
                _errorMessage.value = "Error: Verifica tu correo o contraseña."
            }

            _isLoading.value = false
        }
    }
}
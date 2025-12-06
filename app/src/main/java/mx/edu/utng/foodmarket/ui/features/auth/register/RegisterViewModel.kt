package mx.edu.utng.foodmarket.ui.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.UsuarioApp
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Evento de Navegación
    private val _registerEventChannel = Channel<Unit>()
    val registerEvent = _registerEventChannel.receiveAsFlow()

    // Función que recibe TODOS los datos de tu formulario
    fun register(
        username: String,
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        pass: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val nuevoUsuario = UsuarioApp(
                username = username,
                nombre = "$nombre $apellido", // Combinamos nombre y apellido
                email = email,
                telefono = telefono,
                rol = "CLIENTE"
            )

            val result = authRepository.registrarUsuario(
                email = email,
                pass = pass,
                datosUsuario = nuevoUsuario
            )

            result.onSuccess {
                _registerEventChannel.send(Unit)
            }

            result.onFailure { e ->
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }

            _isLoading.value = false
        }
    }
}
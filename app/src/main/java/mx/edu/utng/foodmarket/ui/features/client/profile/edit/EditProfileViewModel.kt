package mx.edu.utng.foodmarket.ui.features.client.profile.edit

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
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var currentUser: UsuarioApp? = null

    // Estados del Formulario
    val username = MutableStateFlow("")
    val nombre = MutableStateFlow("")
    val telefono = MutableStateFlow("")

    // Estados de UI
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Evento de Éxito
    private val _updateEventChannel = Channel<Unit>()
    val updateEvent = _updateEventChannel.receiveAsFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.obtenerUsuarioActual().onSuccess { user ->
                currentUser = user
                username.value = user.username
                nombre.value = user.nombre
                telefono.value = user.telefono
            }.onFailure {
                _errorMessage.value = "Error al cargar datos"
            }
            _isLoading.value = false
        }
    }

    fun guardarCambios() {
        val user = currentUser ?: return

        // Validaciones básicas
        if (nombre.value.isBlank() || username.value.isBlank()) {
            _errorMessage.value = "El nombre y usuario no pueden estar vacíos"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            val usuarioActualizado = user.copy(
                username = username.value,
                nombre = nombre.value,
                telefono = telefono.value
            )

            val resultado = authRepository.actualizarUsuario(usuarioActualizado)

            if (resultado.isSuccess) {
                _updateEventChannel.send(Unit)
            } else {
                _errorMessage.value = "Error al guardar: ${resultado.exceptionOrNull()?.message}"
            }
            _isSaving.value = false
        }
    }
}
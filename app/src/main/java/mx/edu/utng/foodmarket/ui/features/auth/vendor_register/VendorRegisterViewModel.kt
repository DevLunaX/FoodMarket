package mx.edu.utng.foodmarket.ui.features.auth.vendor_register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.data.model.UsuarioApp
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class VendorRegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // --- ESTADOS DEL FORMULARIO ---
    val nombreNegocio = MutableStateFlow("")
    val nombrePropietario = MutableStateFlow("")
    val email = MutableStateFlow("")
    val telefono = MutableStateFlow("")
    val direccion = MutableStateFlow("")
    val descripcion = MutableStateFlow("")
    val tipoNegocio = MutableStateFlow("") // Categoría
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    // Ubicación (LatLng de Google Maps)
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    // --- ESTADOS DE UI ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Evento de Navegación
    private val _registerEventChannel = Channel<Unit>()
    val registerEvent = _registerEventChannel.receiveAsFlow()

    // --- SETTERS ---
    fun setLocation(loc: LatLng) { _selectedLocation.value = loc }

    // --- LÓGICA DE REGISTRO ---
    fun register() {
        val loc = _selectedLocation.value

        // 1. Validaciones
        if (nombreNegocio.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
            _errorMessage.value = "Faltan datos obligatorios"
            return
        }
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Las contraseñas no coinciden"
            return
        }
        if (loc == null) {
            _errorMessage.value = "Por favor selecciona la ubicación en el mapa"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // 2. Crear Objeto Usuario
            val nuevoUsuario = UsuarioApp(
                email = email.value,
                nombre = nombrePropietario.value,
                rol = "VENDEDOR",
                telefono = telefono.value,
                username = "" // Opcional
            )

            // 3. Crear Objeto Negocio
            val nuevoNegocio = NegocioApp(
                nombreNegocio = nombreNegocio.value,
                categoria = tipoNegocio.value,
                direccion = direccion.value,
                descripcion = descripcion.value,
                telefono = telefono.value,
                latitud = loc.latitude,
                longitud = loc.longitude
            )

            // 4. Llamar al Repositorio
            val result = authRepository.registrarUsuario(
                email = email.value,
                pass = password.value,
                datosUsuario = nuevoUsuario,
                datosNegocio = nuevoNegocio
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
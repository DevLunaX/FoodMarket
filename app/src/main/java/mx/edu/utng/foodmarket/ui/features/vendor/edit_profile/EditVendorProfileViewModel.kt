package mx.edu.utng.foodmarket.ui.features.vendor.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.data.model.UsuarioApp
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import mx.edu.utng.foodmarket.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class EditVendorProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    // Datos Originales (para actualizar)
    private var currentUser: UsuarioApp? = null
    private var currentBusiness: NegocioApp? = null

    // Estados del Formulario (Negocio)
    val businessName = MutableStateFlow("")
    val description = MutableStateFlow("")
    val address = MutableStateFlow("")
    val phone = MutableStateFlow("")

    // Estados del Formulario (Usuario)
    val ownerName = MutableStateFlow("")

    // UI States
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Cargar Usuario
            authRepository.obtenerUsuarioActual().onSuccess { user ->
                currentUser = user
                ownerName.value = user.nombre

                // 2. Cargar Negocio (usamos la función de snapshot para tener los datos frescos)
                storeRepository.escucharMiNegocio { negocio ->
                    if (negocio != null) {
                        currentBusiness = negocio
                        businessName.value = negocio.nombreNegocio
                        description.value = negocio.descripcion
                        address.value = negocio.direccion
                        phone.value = negocio.telefono
                    }
                    // Cuando tenemos ambos, quitamos loading
                    _isLoading.value = false
                }
            }.onFailure {
                _isLoading.value = false
                _errorMessage.value = "No se pudo cargar la información."
            }
        }
    }

    fun guardarCambios() {
        if (currentBusiness == null || currentUser == null) return

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            _saveSuccess.value = false

            // A. Actualizar Usuario
            val usuarioNuevo = currentUser!!.copy(nombre = ownerName.value)
            val resUser = authRepository.actualizarUsuario(usuarioNuevo)

            // B. Actualizar Negocio
            val negocioNuevo = currentBusiness!!.copy(
                nombreNegocio = businessName.value,
                descripcion = description.value,
                direccion = address.value,
                telefono = phone.value
            )
            // Necesitamos agregar esta función al repositorio primero
            val resNegocio = storeRepository.actualizarNegocio(negocioNuevo)

            if (resUser.isSuccess && resNegocio.isSuccess) {
                _saveSuccess.value = true
            } else {
                _errorMessage.value = "Error al guardar cambios."
            }

            _isSaving.value = false
        }
    }

    fun resetSuccess() { _saveSuccess.value = false }
}
package mx.edu.utng.foodmarket.ui.features.vendor.dashboard

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
class VendorPanelViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    // Estado del Negocio (Se actualiza en tiempo real)
    private val _miNegocio = MutableStateFlow<NegocioApp?>(null)
    val miNegocio = _miNegocio.asStateFlow()

    // Estado del Usuario (Dueño)
    private val _miUsuario = MutableStateFlow<UsuarioApp?>(null)
    val miUsuario = _miUsuario.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Obtener datos del usuario logueado
            authRepository.obtenerUsuarioActual().onSuccess { usuario ->
                _miUsuario.value = usuario

                // 2. CORRECCIÓN: Borramos 'usuario.id' de los paréntesis.
                // La función ya sabe quién es el usuario actual internamente.
                storeRepository.escucharMiNegocio { negocio ->
                    _miNegocio.value = negocio
                    _isLoading.value = false
                }
            }.onFailure {
                _isLoading.value = false
            }
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }
}
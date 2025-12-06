package mx.edu.utng.foodmarket.ui.features.vendor.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.data.model.Review
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import mx.edu.utng.foodmarket.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class VendorReviewsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    // --- ESTADOS ---
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // Para saber de qué negocio estamos hablando
    private var currentBusinessId: String? = null

    // Estados para el diálogo de respuesta
    private val _isSendingResponse = MutableStateFlow(false)
    val isSendingResponse = _isSendingResponse.asStateFlow()

    private val _responseSuccess = MutableStateFlow(false)
    val responseSuccess = _responseSuccess.asStateFlow()

    init {
        cargarResenas()
    }

    private fun cargarResenas() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Obtener ID del usuario actual
            authRepository.obtenerUsuarioActual().onSuccess { user ->
                // 2. Escuchar SU negocio
                storeRepository.escucharMiNegocio { negocio ->
                    if (negocio != null) {
                        currentBusinessId = negocio.id
                        // 3. Escuchar las reseñas de ESE negocio
                        storeRepository.escucharResenas(negocio.id) { listaResenas ->
                            _reviews.value = listaResenas
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
    }

    fun enviarRespuesta(reviewId: String, respuesta: String) {
        if (respuesta.isBlank()) return

        viewModelScope.launch {
            _isSendingResponse.value = true
            _responseSuccess.value = false

            // Llamamos al repositorio (necesitamos agregar esta función ahí)
            storeRepository.responderResena(reviewId, respuesta)
                .onSuccess {
                    _responseSuccess.value = true
                }
                .onFailure {
                    // Manejar error si quieres
                }

            _isSendingResponse.value = false
        }
    }

    fun resetResponseState() {
        _responseSuccess.value = false
    }
}
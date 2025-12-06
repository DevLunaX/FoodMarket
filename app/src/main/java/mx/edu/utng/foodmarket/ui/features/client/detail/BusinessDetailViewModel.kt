package mx.edu.utng.foodmarket.ui.features.client.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.data.model.Review
import mx.edu.utng.foodmarket.data.model.UsuarioApp
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import mx.edu.utng.foodmarket.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class BusinessDetailViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- ESTADOS ---
    private val _negocio = MutableStateFlow<NegocioApp?>(null)
    val negocio = _negocio.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val _currentUser = MutableStateFlow<UsuarioApp?>(null)
    // No necesitamos exponer el usuario a la UI, lo usamos internamente para guardar la reseña

    init {
        val businessId = savedStateHandle.get<String>("businessId")
        if (businessId != null) {
            cargarDatos(businessId)
        }
        cargarUsuario()
    }

    private fun cargarDatos(id: String) {
        // 1. Cargar Negocio
        storeRepository.obtenerNegocioPorId(id) {
            _negocio.value = it
        }
        // 2. Cargar Reseñas
        storeRepository.escucharResenas(id) {
            _reviews.value = it
        }
        // 3. Verificar si es favorito
        viewModelScope.launch {
            _isFavorite.value = storeRepository.esFavorito(id)
        }
    }

    private fun cargarUsuario() {
        viewModelScope.launch {
            authRepository.obtenerUsuarioActual().onSuccess { _currentUser.value = it }
        }
    }

    // --- ACCIONES DE UI ---

    // 1. Registrar Visita (Se llama al dar clic en "Cómo llegar")
    fun registrarVisita() {
        val currentId = _negocio.value?.id ?: return
        viewModelScope.launch {
            storeRepository.registrarVisita(currentId)
        }
    }

    // 2. Toggle Favorito
    fun toggleFavorite() {
        val currentNegocio = _negocio.value ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                storeRepository.eliminarFavorito(currentNegocio.id)
                _isFavorite.value = false
            } else {
                storeRepository.agregarFavorito(currentNegocio)
                _isFavorite.value = true
            }
        }
    }

    // 3. Enviar Reseña
    fun submitReview(rating: Int, comment: String) {
        val currentNegocio = _negocio.value ?: return
        val user = _currentUser.value ?: return // Necesitamos estar logueados

        viewModelScope.launch {
            val nuevaResena = Review(
                businessId = currentNegocio.id,
                userId = user.id,
                userName = user.nombre.ifBlank { user.username },
                rating = rating,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            storeRepository.subirResena(nuevaResena)
        }
    }
}
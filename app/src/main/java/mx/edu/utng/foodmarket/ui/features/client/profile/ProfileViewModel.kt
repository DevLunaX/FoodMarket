package mx.edu.utng.foodmarket.ui.features.client.profile

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
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    // Datos del Usuario
    private val _user = MutableStateFlow<UsuarioApp?>(null)
    val user = _user.asStateFlow()

    // Contadores
    private val _stats = MutableStateFlow(ProfileStats())
    val stats = _stats.asStateFlow()

    // Listas para los Sheets
    private val _myReviews = MutableStateFlow<List<Review>>(emptyList())
    val myReviews = _myReviews.asStateFlow()

    private val _myVisits = MutableStateFlow<List<NegocioApp>>(emptyList())
    val myVisits = _myVisits.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Obtener Usuario
            authRepository.obtenerUsuarioActual().onSuccess {
                _user.value = it
            }

            // 2. Obtener Contadores (Paralelo)
            val favs = storeRepository.contarFavoritos()
            val reviews = storeRepository.contarMisResenas()
            val visits = storeRepository.contarVisitasPersonales()

            _stats.value = ProfileStats(favs, reviews, visits)

            _isLoading.value = false
        }
    }

    fun cargarResenas() {
        viewModelScope.launch {
            storeRepository.obtenerMisResenasList().onSuccess { _myReviews.value = it }
        }
    }

    fun cargarVisitas() {
        viewModelScope.launch {
            storeRepository.obtenerMisVisitasList().onSuccess { _myVisits.value = it }
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
    }
}

data class ProfileStats(
    val favoritos: Int = 0,
    val resenas: Int = 0,
    val visitas: Int = 0
)
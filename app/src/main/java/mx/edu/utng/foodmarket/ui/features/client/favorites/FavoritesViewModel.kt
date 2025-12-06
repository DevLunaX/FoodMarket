package mx.edu.utng.foodmarket.ui.features.client.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<NegocioApp>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        viewModelScope.launch {
            _isLoading.value = true
            val resultado = repository.obtenerFavoritos()
            resultado.onSuccess {
                _favorites.value = it
            }
            _isLoading.value = false
        }
    }

    fun eliminarFavorito(negocio: NegocioApp) {
        viewModelScope.launch {
            // Optimista: Lo quitamos de la lista visual inmediatamente
            val listaActual = _favorites.value.toMutableList()
            listaActual.remove(negocio)
            _favorites.value = listaActual

            // Luego lo borramos de la base de datos
            repository.eliminarFavorito(negocio.id)
        }
    }
}
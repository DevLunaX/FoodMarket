package mx.edu.utng.foodmarket.ui.features.client.map

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
class MapViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    // Estado: Lista de negocios
    private val _negocios = MutableStateFlow<List<NegocioApp>>(emptyList())
    val negocios = _negocios.asStateFlow()

    init {
        // Al crearse el ViewModel, empezamos a escuchar cambios en la BD
        cargarNegocios()
    }

    private fun cargarNegocios() {
        // Esta función del repositorio ya maneja snapshots en tiempo real
        storeRepository.escucharNegociosEnTiempoReal { lista ->
            _negocios.value = lista
        }
    }
}
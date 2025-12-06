package mx.edu.utng.foodmarket.ui.features.vendor.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.data.repository.AuthRepository
import mx.edu.utng.foodmarket.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class VendorStatisticsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    // Datos reales del negocio
    private val _negocio = MutableStateFlow<NegocioApp?>(null)
    val negocio = _negocio.asStateFlow()

    // Filtro de tiempo seleccionado (UI state)
    private val _selectedPeriod = MutableStateFlow("Esta Semana")
    val selectedPeriod = _selectedPeriod.asStateFlow()

    // Puntuación de Salud (Calculada)
    private val _healthScore = MutableStateFlow(0)
    val healthScore = _healthScore.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            authRepository.obtenerUsuarioActual().onSuccess { user ->
                storeRepository.escucharMiNegocio { miNegocio ->
                    _negocio.value = miNegocio
                    calcularSalud(miNegocio)
                }
            }
        }
    }

    fun setPeriod(period: String) {
        _selectedPeriod.value = period
        // Aquí podrías filtrar datos históricos si tuvieras esa tabla
    }

    private fun calcularSalud(negocio: NegocioApp) {
        // Algoritmo simple para calcular "Salud del Negocio"
        // Base 50 puntos + (Rating * 10)
        var score = 50 + (negocio.ratingPromedio * 10).toInt()

        // Bonus por visitas
        if (negocio.visitas > 100) score += 10
        if (negocio.totalResenas > 5) score += 5

        // Tope 100
        if (score > 100) score = 100

        _healthScore.value = score
    }
}
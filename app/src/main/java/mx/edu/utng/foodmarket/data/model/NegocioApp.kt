package mx.edu.utng.foodmarket.data.model

data class NegocioApp(
    val id: String = "",
    val ownerId: String = "",
    val nombreNegocio: String = "",
    val categoria: String = "",
    val direccion: String = "",
    val descripcion: String = "",
    val telefono: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val visitas: Int = 0,
    val ratingPromedio: Double = 0.0,
    val totalResenas: Int = 0
)
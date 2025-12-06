package mx.edu.utng.foodmarket.data.repository


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.data.model.Review

class StoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance() // Necesario para saber ID del usuario en favoritos/reviews

    // ==========================================
    // 1. GESTIÓN DE NEGOCIOS (CRUD y Lectura)
    // ==========================================

    // VENDEDOR: Actualizar datos de su local
    suspend fun actualizarNegocio(negocioActualizado: NegocioApp): Result<Unit> {
        return try {
            db.collection("businesses").document(negocioActualizado.id).set(negocioActualizado).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    // CLIENTE: Escuchar lista para el Mapa (Todos los negocios)
    fun escucharNegociosEnTiempoReal(onDatosActualizados: (List<NegocioApp>) -> Unit) {
        db.collection("businesses")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    val lista = snapshots.toObjects(NegocioApp::class.java)
                    // Filtro de coordenadas válidas
                    val validos = lista.filter { it.latitud != 0.0 && it.longitud != 0.0 }
                    onDatosActualizados(validos)
                }
            }
    }

    // VENDEDOR: Escuchar SU propio negocio
    fun escucharMiNegocio(onDatosActualizados: (NegocioApp) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("businesses")
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    val negocio = snapshot.documents[0].toObject(NegocioApp::class.java)
                    if (negocio != null) {
                        onDatosActualizados(negocio)
                    }
                }
            }
    }

    // ==========================================
    // 2. RESEÑAS Y ESTADÍSTICAS
    // ==========================================

    suspend fun subirResena(review: Review): Result<Unit> {
        return try {
            val docRef = db.collection("reviews").document()
            val reviewConId = review.copy(id = docRef.id)
            docRef.set(reviewConId).await()

            recalcularPromedio(review.businessId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun recalcularPromedio(businessId: String) {
        try {
            val snapshot = db.collection("reviews")
                .whereEqualTo("businessId", businessId)
                .get().await()

            val reviews = snapshot.toObjects(Review::class.java)

            if (reviews.isNotEmpty()) {
                val totalEstrellas = reviews.sumOf { it.rating }
                val totalVotos = reviews.size
                val nuevoPromedio = totalEstrellas.toDouble() / totalVotos

                db.collection("businesses").document(businessId)
                    .update(mapOf(
                        "ratingPromedio" to nuevoPromedio,
                        "totalResenas" to totalVotos
                    )).await()
            }
        } catch (e: Exception) { }
    }

    fun escucharResenas(businessId: String, onReviewsUpdate: (List<Review>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("businessId", businessId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                if (snapshots != null) {
                    onReviewsUpdate(snapshots.toObjects(Review::class.java))
                }
            }
    }

    suspend fun registrarVisita(businessId: String) {
        try {
            db.collection("businesses").document(businessId)
                .update("visitas", FieldValue.increment(1))
                .await()

            val userId = auth.currentUser?.uid ?: return
            val visitaData = hashMapOf("businessId" to businessId, "timestamp" to System.currentTimeMillis())
            db.collection("users").document(userId)
                .collection("visits").document(businessId).set(visitaData).await()
        } catch (e: Exception) { }
    }

    // ==========================================
    // 3. FAVORITOS E INTERACCIONES DE USUARIO
    // ==========================================

    // Aunque esto se guarda dentro de "users", conceptualmente es
    // "gestión de tiendas favoritas", así que vive bien aquí.

    suspend fun agregarFavorito(negocio: NegocioApp): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("No logueado")
            db.collection("users").document(userId)
                .collection("favorites").document(negocio.id).set(negocio).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun eliminarFavorito(negocioId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("No logueado")
            db.collection("users").document(userId)
                .collection("favorites").document(negocioId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun esFavorito(negocioId: String): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val doc = db.collection("users").document(userId)
                .collection("favorites").document(negocioId).get().await()
            doc.exists()
        } catch (e: Exception) { false }
    }

    suspend fun obtenerFavoritos(): Result<List<NegocioApp>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("No logueado")
            val snapshot = db.collection("users").document(userId)
                .collection("favorites").get().await()
            Result.success(snapshot.toObjects(NegocioApp::class.java))
        } catch (e: Exception) { Result.failure(e) }
    }

    // ==========================================
    // 4. CONTEOS Y LISTAS PERSONALES
    // ==========================================

    suspend fun contarFavoritos(): Int {
        return try {
            val userId = auth.currentUser?.uid ?: return 0
            val snapshot = db.collection("users").document(userId).collection("favorites").get().await()
            snapshot.size()
        } catch (e: Exception) { 0 }
    }

    suspend fun contarMisResenas(): Int {
        return try {
            val userId = auth.currentUser?.uid ?: return 0
            val snapshot = db.collection("reviews").whereEqualTo("userId", userId).get().await()
            snapshot.size()
        } catch (e: Exception) { 0 }
    }

    suspend fun contarVisitasPersonales(): Int {
        return try {
            val userId = auth.currentUser?.uid ?: return 0
            val snapshot = db.collection("users").document(userId).collection("visits").get().await()
            snapshot.size()
        } catch (e: Exception) { 0 }
    }

    suspend fun obtenerMisResenasList(): Result<List<Review>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("No logueado")
            val snapshot = db.collection("reviews").whereEqualTo("userId", userId).get().await()
            Result.success(snapshot.toObjects(Review::class.java))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun obtenerMisVisitasList(): Result<List<NegocioApp>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("No logueado")
            val visitsSnapshot = db.collection("users").document(userId).collection("visits").get().await()
            val businessIds = visitsSnapshot.documents.map { it.id }

            if (businessIds.isEmpty()) return Result.success(emptyList())

            // Nota: 'whereIn' solo soporta hasta 10 elementos.
            // Para una app real grande se necesitaría otra lógica, pero para escolar está bien.
            val businessesSnapshot = db.collection("businesses")
                .whereIn("id", businessIds.take(10))
                .get().await()
            Result.success(businessesSnapshot.toObjects(NegocioApp::class.java))
        } catch (e: Exception) { Result.failure(e) }
    }

    // OBTENER NEGOCIO POR ID (Para la pantalla de detalle)
    fun obtenerNegocioPorId(businessId: String, onResult: (NegocioApp?) -> Unit) {
        db.collection("businesses").document(businessId)
            .addSnapshotListener { snapshot, _ ->
                val negocio = snapshot?.toObject(NegocioApp::class.java)
                onResult(negocio)
            }
    }
    // RESPONDER A UNA RESEÑA
    suspend fun responderResena(reviewId: String, respuesta: String): Result<Unit> {
        return try {
            db.collection("reviews").document(reviewId)
                .update(mapOf(
                    "response" to respuesta,
                    "responseTimestamp" to System.currentTimeMillis()
                )).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
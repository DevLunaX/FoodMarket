package mx.edu.utng.foodmarket.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import kotlinx.coroutines.tasks.await
import mx.edu.utng.foodmarket.data.model.UsuarioApp;
import mx.edu.utng.foodmarket.data.model.NegocioApp;

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 1. LOGIN
    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val userId = authResult.user?.uid ?: throw Exception("ID nulo")
            // Buscamos el rol para saber a dónde redirigir
            val document = db.collection("users").document(userId).get().await()
            val rol = document.getString("rol") ?: "CLIENTE"
            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. REGISTRO (Maneja Usuario y creación inicial del Negocio si aplica)
    suspend fun registrarUsuario(
            email: String,
            pass: String,
            datosUsuario: UsuarioApp,
            datosNegocio: NegocioApp? = null
    ): Result<String> {
        return try {
            // A. Crear en Auth
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val userId = authResult.user?.uid ?: throw Exception("Error al obtener ID")

            // B. Guardar en 'users'
            val usuarioAGuardar = datosUsuario.copy(id = userId)
            db.collection("users").document(userId).set(usuarioAGuardar).await()

            // C. Si es Vendedor, crear documento en 'businesses'
            if (datosUsuario.rol == "VENDEDOR" && datosNegocio != null) {
                val negocioRef = db.collection("businesses").document()
                val negocioAGuardar = datosNegocio.copy(
                        id = negocioRef.id,
                        ownerId = userId
                )
                negocioRef.set(negocioAGuardar).await()
            }

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. OBTENER DATOS DE USUARIO (Esta faltaba en el ejemplo anterior)
    suspend fun obtenerUsuarioActual(): Result<UsuarioApp> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("No hay sesión")
            val document = db.collection("users").document(userId).get().await()
            val usuario = document.toObject(UsuarioApp::class.java) ?: throw Exception("Sin datos")
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. ACTUALIZAR PERFIL (Esta faltaba en el ejemplo anterior)
    suspend fun actualizarUsuario(usuarioActualizado: UsuarioApp): Result<Unit> {
        return try {
            db.collection("users").document(usuarioActualizado.id).set(usuarioActualizado).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. UTILIDADES
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun cerrarSesion() {
        auth.signOut()
    }
}
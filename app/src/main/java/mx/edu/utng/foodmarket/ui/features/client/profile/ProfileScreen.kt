package mx.edu.utng.foodmarket.ui.features.client.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mx.edu.utng.foodmarket.ui.components.AppBottomNavigationBar
import mx.edu.utng.foodmarket.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    onNavigate: (String) -> Unit,
    // Conectamos el ViewModel aquí
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // === ESTADOS DEL VIEWMODEL (Datos vivos) ===
    val usuarioData by viewModel.user.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val myReviewsList by viewModel.myReviews.collectAsState()
    val myVisitsList by viewModel.myVisits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // === ESTADOS LOCALES DE UI ===
    var showReviewsSheet by remember { mutableStateOf(false) }
    var showVisitsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Amber500)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // === TU HEADER ORIGINAL ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Amber500, Amber600)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.White, CircleShape)
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(Amber100),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = Amber600
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Nombre
                        Text(
                            text = usuarioData?.username ?: usuarioData?.nombre ?: "Usuario",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Editar
                        Button(
                            onClick = { onNavigate("edit_profile") }, // Asegúrate de tener esta ruta o cámbiala
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Amber600
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Icon(Icons.Outlined.Edit, null, Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar Perfil", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // === CUERPO (TUS TARJETAS ORIGINALES) ===
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

                    // Estadísticas (Conectadas a stats del ViewModel)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard(Icons.Filled.Favorite, stats.favoritos.toString(), "Favoritos", Pink500)
                        StatCard(Icons.Filled.Star, stats.resenas.toString(), "Reseñas", Amber500)
                        StatCard(Icons.Filled.ShoppingBag, stats.visitas.toString(), "Visitas", Blue500)
                    }

                    // Mi Actividad
                    Text(
                        "Mi Actividad",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray700,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            ModernProfileOption(
                                Icons.Filled.Favorite,
                                "Mis Favoritos",
                                "${stats.favoritos} negocios guardados",
                                Pink500
                            ) { onNavigate("favorites_screen") }

                            HorizontalDivider(color = Gray100, modifier = Modifier.padding(horizontal = 16.dp))

                            // Botón Reseñas (Llama al ViewModel)
                            ModernProfileOption(
                                Icons.Filled.Star,
                                "Mis Reseñas",
                                "${stats.resenas} reseñas escritas",
                                Amber500
                            ) {
                                viewModel.cargarResenas()
                                showReviewsSheet = true
                            }

                            HorizontalDivider(color = Gray100, modifier = Modifier.padding(horizontal = 16.dp))

                            // Botón Visitas (Llama al ViewModel)
                            ModernProfileOption(
                                Icons.Filled.LocationOn,
                                "Lugares Visitados",
                                "${stats.visitas} negocios",
                                Blue500
                            ) {
                                viewModel.cargarVisitas()
                                showVisitsSheet = true
                            }
                        }
                    }

                    // Preferencias
                    Text(
                        "Preferencias",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray700,
                        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            ModernProfileOption(Icons.Filled.Notifications, "Notificaciones", "Gestionar alertas", Blue500) { }
                            HorizontalDivider(color = Gray100, modifier = Modifier.padding(horizontal = 16.dp))
                            ModernProfileOption(Icons.Filled.Language, "Idioma", "Español", Green600) { }
                        }
                    }

                    // Botón Cerrar Sesión
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(bottom = 100.dp), // Espacio extra para el menú flotante
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Cerrar Sesión",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Navbar Flotante (Manteniendo la estructura nueva)
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            AppBottomNavigationBar(currentRoute = "profile_screen", onNavigate = onNavigate)
        }
    }

    // === TUS SHEETS ORIGINALES ===

    // Sheet Reseñas
    if (showReviewsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReviewsSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                Text("Mis Reseñas", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Gray900)
                Spacer(modifier = Modifier.height(16.dp))

                if (myReviewsList.isEmpty()) {
                    Text("No has escrito reseñas aún.", color = Gray500)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(myReviewsList) { review ->
                            Card(colors = CardDefaults.cardColors(containerColor = Gray50), shape = RoundedCornerShape(12.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row {
                                        repeat(review.rating) { Icon(Icons.Filled.Star, null, tint = Amber500, modifier = Modifier.size(16.dp)) }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(review.comment.ifBlank { "Sin comentario" }, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Sheet Visitas
    if (showVisitsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVisitsSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                Text("Lugares Visitados", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Gray900)
                Spacer(modifier = Modifier.height(16.dp))

                if (myVisitsList.isEmpty()) {
                    Text("No has visitado lugares aún.", color = Gray500)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(myVisitsList) { negocio ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(40.dp).background(Blue100, CircleShape), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Store, null, tint = Blue500)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(negocio.nombreNegocio, fontWeight = FontWeight.Bold)
                                        Text(negocio.categoria, fontSize = 12.sp, color = Gray500)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Filled.CheckCircle, null, tint = Green600)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("¿Cerrar sesión?", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cerrarSesion()
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Cerrar Sesión") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") } }
        )
    }
}

// === TUS HELPERS VISUALES ORIGINALES ===

@Composable
fun StatCard(icon: ImageVector, value: String, label: String, color: Color) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gray700)
            Text(label, fontSize = 12.sp, color = Gray600)
        }
    }
}

@Composable
fun ModernProfileOption(icon: ImageVector, text: String, description: String, iconColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Gray700)
            Text(description, fontSize = 13.sp, color = Gray600)
        }
        Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = Gray400)
    }
}
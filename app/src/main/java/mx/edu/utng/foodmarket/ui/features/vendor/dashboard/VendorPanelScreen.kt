package mx.edu.utng.foodmarket.ui.features.vendor.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mx.edu.utng.foodmarket.ui.theme.*

@Composable
fun VendorPanelScreen(
    onExit: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToReviews: () -> Unit,
    // Inyección del ViewModel
    viewModel: VendorPanelViewModel = hiltViewModel()
) {
    // === ESTADOS DEL VIEWMODEL ===
    val miNegocio by viewModel.miNegocio.collectAsState()
    val miUsuario by viewModel.miUsuario.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = Gray50
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue700)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // === 1. HEADER MODERNO ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Gray800, Gray900)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Store, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Bienvenido,",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                                // Nombre del Vendedor
                                Text(
                                    text = miUsuario?.nombre ?: "Vendedor",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Botón Cerrar Sesión
                            IconButton(
                                onClick = {
                                    viewModel.cerrarSesion()
                                    onExit()
                                },
                                modifier = Modifier.background(Color.Red.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Nombre del Negocio
                        Text(
                            text = miNegocio?.nombreNegocio ?: "Registra tu Negocio",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // === 2. TARJETA DE ESTADÍSTICAS ===
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-30).dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // VISUALIZACIONES
                        StatItemVendor(
                            value = (miNegocio?.visitas ?: 0).toString(),
                            label = "Visitas Totales",
                            icon = Icons.Default.Visibility,
                            color = Blue600
                        )

                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Gray200))

                        // CALIFICACIÓN
                        val rating = miNegocio?.ratingPromedio ?: 0.0
                        val ratingTexto = if (rating > 0.0) {
                            "%.1f".format(rating)
                        } else {
                            "-"
                        }

                        StatItemVendor(
                            value = ratingTexto,
                            label = "Calificación",
                            icon = Icons.Default.Star,
                            color = Amber500
                        )
                    }
                }

                // === 3. MENÚ DE OPCIONES ===
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Gestión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray800,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val menuItems = listOf(
                        DashboardOption("Editar Perfil", "Actualiza tu info", Icons.Outlined.Edit, Blue500, onNavigateToEditProfile),
                        DashboardOption("Reseñas", "Lee opiniones", Icons.Outlined.RateReview, Amber500, onNavigateToReviews),
                        DashboardOption("Estadísticas", "Ver métricas", Icons.Outlined.BarChart, Green600, onNavigateToStatistics),
                        DashboardOption("Soporte", "Ayuda técnica", Icons.Outlined.SupportAgent, Gray600) { /* TODO */ }
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(menuItems) { item ->
                            DashboardCard(item)
                        }
                    }
                }
            }
        }
    }
}

// === COMPONENTES AUXILIARES ===

@Composable
fun StatItemVendor(value: String, label: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Gray900)
        Text(text = label, fontSize = 12.sp, color = Gray500)
    }
}

data class DashboardOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun DashboardCard(item: DashboardOption) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = item.onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(item.color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, null, tint = item.color)
            }

            Column {
                Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Gray900)
                Text(text = item.subtitle, fontSize = 12.sp, color = Gray500)
            }
        }
    }
}
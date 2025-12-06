package mx.edu.utng.foodmarket.ui.features.vendor.statistics

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mx.edu.utng.foodmarket.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewStatisticsScreen(
    onBack: () -> Unit,
    viewModel: VendorStatisticsViewModel = hiltViewModel()
) {
    // === ESTADOS ===
    val negocio by viewModel.negocio.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val healthScore by viewModel.healthScore.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF5F5F7), // Fondo gris estilo iOS
        topBar = {
            TopAppBar(
                title = { Text("Panel de Rendimiento", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. FILTRO DE TIEMPO
            TimeFilterSection(
                selectedPeriod = selectedPeriod,
                onSelect = { viewModel.setPeriod(it) }
            )

            // 2. SALUD DEL NEGOCIO (Dato Real Calculado)
            BusinessHealthCard(score = healthScore)

            Text("Métricas Clave", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // 3. TARJETAS DE ESTADÍSTICAS (Conectadas a Firebase)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Visitas Totales",
                    value = "${negocio?.visitas ?: 0}",
                    change = "+View", // Podrías calcular esto si tuvieras historial
                    icon = Icons.Outlined.Visibility,
                    color = Blue500,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Calificación",
                    value = String.format("%.1f", negocio?.ratingPromedio ?: 0.0),
                    change = "${negocio?.totalResenas ?: 0} votos",
                    icon = Icons.Outlined.Star, // Cambié a Star porque es rating
                    color = Amber500, // Color Amber para estrellas
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Estos datos son simulados porque no están en NegocioApp aun
                StatCard("Llamadas", "23", "+15%", Icons.Outlined.Phone, Green600, Modifier.weight(1f))
                StatCard("Ruta GPS", "67", "+5%", Icons.Outlined.Map, Pink500, Modifier.weight(1f))
            }

            // 4. SECCIONES "PRÓXIMAMENTE" (Tus diseños borrosos originales)
            Text("Análisis Avanzado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Productos Más Vistos (Bloqueado)
            ComingSoonSection(title = "Productos Más Populares") {
                Column {
                    ProductStatRow("Pan Blanco", 45, Blue500)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProductStatRow("Conchas", 38, Purple40) // Usé Purple40 del tema
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    ProductStatRow("Pan Integral", 29, Green600)
                }
            }

            // Horarios (Bloqueado)
            ComingSoonSection(title = "Horarios Pico") {
                Column {
                    TimeSlotBar("8:00 - 10:00 AM", 0.85f)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeSlotBar("10:00 - 12:00 PM", 0.65f)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeSlotBar("4:00 - 6:00 PM", 0.75f)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ============================================
// COMPONENTES DE UI
// ============================================

@Composable
fun TimeFilterSection(selectedPeriod: String, onSelect: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Hoy", "Esta Semana", "Este Mes").forEach { period ->
                val isSelected = selectedPeriod == period
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Gray900 else Color.Transparent) // Gray900 para contraste
                        .clickable { onSelect(period) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BusinessHealthCard(score: Int) {
    val (statusText, statusColor, icon) = when {
        score >= 80 -> Triple("Excelente", Green600, Icons.Rounded.SentimentVerySatisfied)
        score >= 50 -> Triple("Regular", Amber500, Icons.Rounded.SentimentSatisfied)
        else -> Triple("Atención", MaterialTheme.colorScheme.error, Icons.Rounded.SentimentVeryDissatisfied)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Gray900), // Oscuro elegante
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Salud del Negocio", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tu desempeño es $statusText", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Basado en visitas recientes y calificaciones.",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(70.dp),
                    color = Color.DarkGray,
                    strokeWidth = 6.dp,
                )
                CircularProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.size(70.dp),
                    color = statusColor,
                    strokeWidth = 6.dp,
                )
                Icon(icon, contentDescription = null, tint = statusColor, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
fun ComingSoonSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.Center) {
                // Contenido borroso
                Box(
                    modifier = Modifier
                        .alpha(0.15f)
                        .then(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                Modifier.blur(10.dp)
                            else Modifier
                        )
                ) {
                    content()
                }

                // Overlay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Amber100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Build, contentDescription = null, tint = Amber800)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Estamos trabajando en ello",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "Próximamente disponible",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    change: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(title, fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(4.dp))

            // Lógica simple para color del cambio
            val isPositive = !change.contains("-")
            val changeColor = if (isPositive) Green600 else MaterialTheme.colorScheme.error
            val changeIcon = if (isPositive) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    changeIcon,
                    null,
                    tint = changeColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    change,
                    fontSize = 11.sp,
                    color = changeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProductStatRow(name: String, views: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontWeight = FontWeight.Medium, color = Color.Gray)
        Text("$views", color = color.copy(alpha=0.8f), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TimeSlotBar(time: String, percentage: Float) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(time, fontSize = 12.sp, color = Color.Gray)
            Text("${(percentage * 100).toInt()}%", fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = Gray600.copy(alpha = 0.5f),
            trackColor = Gray200,
        )
    }
}
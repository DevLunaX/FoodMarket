package mx.edu.utng.foodmarket.ui.features.vendor.reviews

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mx.edu.utng.foodmarket.data.model.Review
import mx.edu.utng.foodmarket.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewReviewsScreen(
    onBack: () -> Unit,
    viewModel: VendorReviewsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // === ESTADOS DEL VIEWMODEL ===
    val reviewsList by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSendingResponse by viewModel.isSendingResponse.collectAsState()
    val responseSuccess by viewModel.responseSuccess.collectAsState()

    // Estados Locales UI
    var showResponseDialog by remember { mutableStateOf(false) }
    var reviewToRespond by remember { mutableStateOf<Review?>(null) }
    var responseText by remember { mutableStateOf("") }

    // Efecto para mostrar Toast de éxito
    LaunchedEffect(responseSuccess) {
        if (responseSuccess) {
            Toast.makeText(context, "Respuesta enviada", Toast.LENGTH_SHORT).show()
            showResponseDialog = false
            responseText = ""
            viewModel.resetResponseState()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F7), // Fondo gris suave
        topBar = {
            TopAppBar(
                title = { Text("Reseñas y Opiniones", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
            val totalReviews = reviewsList.size
            val averageRating = if (totalReviews > 0) reviewsList.map { it.rating }.average() else 0.0

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECCIÓN 1: RESUMEN GENERAL
                item {
                    ReviewSummaryCard(averageRating, totalReviews, reviewsList)
                }

                // SECCIÓN 2: LISTA DE RESEÑAS
                if (reviewsList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Aún no tienes reseñas.", color = Color.Gray)
                        }
                    }
                } else {
                    items(reviewsList) { review ->
                        ReviewItemCard(
                            review = review,
                            onReplyClick = {
                                reviewToRespond = review
                                showResponseDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO PARA RESPONDER
    if (showResponseDialog && reviewToRespond != null) {
        AlertDialog(
            onDismissRequest = {
                showResponseDialog = false
                responseText = ""
            },
            title = { Text("Responder a ${reviewToRespond?.userName}") },
            text = {
                Column {
                    Text(
                        "Tu respuesta será pública y visible para todos los clientes.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = responseText,
                        onValueChange = { responseText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        placeholder = { Text("Escribe aquí tu respuesta...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Llamada al ViewModel
                        viewModel.enviarRespuesta(reviewToRespond!!.id, responseText)
                    },
                    enabled = responseText.isNotBlank() && !isSendingResponse,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    if (isSendingResponse) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    } else {
                        Text("Publicar Respuesta")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showResponseDialog = false
                    responseText = ""
                },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = Color.White
        )
    }
}

// ==========================================
// COMPONENTES DE UI (Tus componentes originales)
// ==========================================

@Composable
fun ReviewSummaryCard(average: Double, total: Int, reviews: List<Review>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Lado Izquierdo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.1f", average),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                StarRatingBar(rating = average.toInt(), starSize = 18.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$total Opiniones", fontSize = 12.sp, color = Color.Gray)
            }

            // Separador
            Divider(
                modifier = Modifier
                    .height(80.dp)
                    .width(1.dp)
                    .padding(horizontal = 16.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Lado Derecho
            Column(modifier = Modifier.weight(1f)) {
                for (i in 5 downTo 1) {
                    val count = reviews.count { it.rating == i }
                    val progress = if (total > 0) count.toFloat() / total else 0f
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().height(18.dp)
                    ) {
                        Text("$i", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFFFD700), // Dorado
                            trackColor = Color(0xFFEEEEEE),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItemCard(review: Review, onReplyClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.userName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        formatDate(review.timestamp),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                StarRatingBar(rating = review.rating, starSize = 14.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.comment,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ZONA DE RESPUESTA
            if (review.response != null && review.response.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F7), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(40.dp)
                            .background(Color.Black, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Respuesta del propietario",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            review.response,
                            fontSize = 14.sp,
                            color = Color(0xFF444444)
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onReplyClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Responder")
                }
            }
        }
    }
}

// Helpers
@Composable
fun StarRatingBar(rating: Int, starSize: androidx.compose.ui.unit.Dp) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color.LightGray,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
    return sdf.format(Date(timestamp))
}
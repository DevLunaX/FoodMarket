package mx.edu.utng.foodmarket.ui.features.client.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import mx.edu.utng.foodmarket.data.model.Review
import mx.edu.utng.foodmarket.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BusinessDetailScreen(
    onBack: () -> Unit,
    viewModel: BusinessDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // === CONEXIÓN CON VIEWMODEL (Datos vivos) ===
    val negocio by viewModel.negocio.collectAsState()
    val reviewsList by viewModel.reviews.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    var showReviewDialog by remember { mutableStateOf(false) }

    // Cálculos locales para UI
    val promedioRating = if (reviewsList.isNotEmpty()) {
        reviewsList.map { it.rating }.average()
    } else 0.0
    val totalResenas = reviewsList.size

    val favoriteScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = tween(150),
        label = "favoriteScale"
    )

    // Si aún no carga el negocio, mostramos loading
    if (negocio == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Amber500)
        }
        return
    }

    val item = negocio!! // Ya es seguro usarlo

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // === HEADER CON IMAGEN ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        // TODO: Usar item.fotoUrl cuando la tengas en Firebase
                        "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=1000&auto=format&fit=crop"
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Badge de categoría flotante
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 60.dp, end = 20.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp)),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(item.categoria),
                            contentDescription = null,
                            tint = Amber600,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.categoria,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray800
                        )
                    }
                }

                // Información en el header
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    if (promedioRating > 0) {
                        Surface(
                            color = Amber500,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", promedioRating),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Text(
                        text = item.nombreNegocio,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 32.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // === CONTENIDO PRINCIPAL ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 20.dp)
                    .padding(top = 28.dp)
            ) {
                // Handle indicator
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Gray200)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // === TARJETAS DE ESTADÍSTICAS ===
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ModernStatCard(
                        icon = Icons.Outlined.Star,
                        value = String.format("%.1f", promedioRating),
                        label = "Rating",
                        backgroundColor = Amber50,
                        iconColor = Amber600,
                        modifier = Modifier.weight(1f)
                    )
                    ModernStatCard(
                        icon = Icons.Outlined.RateReview,
                        value = "$totalResenas",
                        label = "Reseñas",
                        backgroundColor = Blue100,
                        iconColor = Blue600,
                        modifier = Modifier.weight(1f)
                    )
                    ModernStatCard(
                        icon = Icons.Outlined.LocationOn,
                        value = "Local",
                        label = "Guanajuato",
                        backgroundColor = Color(0xFFE8F5E9),
                        iconColor = Green600,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // === SECCIÓN SOBRE EL LUGAR ===
                Text(
                    text = "Sobre el lugar",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = item.descripcion.ifBlank { "Este negocio aún no ha agregado una descripción." },
                            fontSize = 15.sp,
                            color = if (item.descripcion.isBlank()) Gray400 else Gray700,
                            lineHeight = 24.sp,
                            fontStyle = if (item.descripcion.isBlank()) FontStyle.Italic else FontStyle.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === SECCIÓN INFORMACIÓN DE CONTACTO ===
                Text(
                    text = "Información",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        ModernInfoRow(
                            icon = Icons.Outlined.LocationOn,
                            label = "Dirección",
                            value = item.direccion.ifBlank { "No especificada" },
                            iconBackground = Amber50,
                            iconTint = Amber600
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Gray100
                        )

                        ModernInfoRow(
                            icon = Icons.Outlined.Phone,
                            label = "Teléfono",
                            value = item.telefono.ifBlank { "No disponible" },
                            iconBackground = Blue100,
                            iconTint = Blue600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === SECCIÓN DE RESEÑAS ===
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reseñas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                        Text(
                            text = "$totalResenas opiniones de clientes",
                            fontSize = 13.sp,
                            color = Gray500
                        )
                    }

                    FilledTonalButton(
                        onClick = { showReviewDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Amber100,
                            contentColor = Amber800
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Opinar",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (reviewsList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Amber50, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.RateReview,
                                    contentDescription = null,
                                    tint = Amber500,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sin reseñas aún",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Gray800
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sé el primero en compartir tu experiencia",
                                fontSize = 14.sp,
                                color = Gray500
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        reviewsList.forEach { review ->
                            ModernReviewItem(review)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // === BOTÓN REGRESAR FLOTANTE ===
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
                .shadow(8.dp, CircleShape)
                .background(Color.White, CircleShape)
                .size(44.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Gray800,
                modifier = Modifier.size(22.dp)
            )
        }

        // === BARRA INFERIOR DE ACCIONES ===
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shadowElevation = 20.dp,
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Llamar
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable {
                            if (item.telefono.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${item.telefono}")
                                }
                                context.startActivity(intent)
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = Gray100
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Phone,
                            contentDescription = "Llamar",
                            tint = Gray700,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Botón Cómo llegar
                Button(
                    onClick = {
                        // 1. REGISTRAR VISITA (Lógica MVVM)
                        viewModel.registrarVisita() // <--- ¡AQUÍ ESTÁ LA MAGIA!

                        // 2. ABRIR MAPS
                        val gmmIntentUri = Uri.parse(
                            "google.navigation:q=${item.latitud},${item.longitud}"
                        )
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) { }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        Icons.Outlined.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Cómo llegar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Botón Favorito (Conectado al ViewModel)
                Surface(
                    modifier = Modifier
                        .size(52.dp)
                        .scale(favoriteScale)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.toggleFavorite() // Lógica movida al ViewModel
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isFavorite) Pink100 else Gray100
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Pink500 else Gray500,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    // === DIÁLOGO PARA ESCRIBIR RESEÑA ===
    if (showReviewDialog) {
        ModernReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                viewModel.submitReview(rating, comment) // Lógica movida al ViewModel
                showReviewDialog = false
            }
        )
    }
}

// === FUNCIONES AUXILIARES Y COMPONENTES (Tus mismos componentes bonitos) ===

private fun getCategoryIcon(categoria: String): ImageVector {
    return when (categoria.lowercase()) {
        "restaurante" -> Icons.Outlined.Restaurant
        "cafetería" -> Icons.Outlined.LocalCafe
        "panadería" -> Icons.Outlined.BakeryDining
        "tienda" -> Icons.Outlined.ShoppingBag
        "servicios" -> Icons.Outlined.Build
        else -> Icons.Outlined.Store
    }
}

@Composable
private fun ModernStatCard(
    icon: ImageVector, value: String, label: String, backgroundColor: Color, iconColor: Color, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.size(44.dp).background(backgroundColor, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Gray900)
            Text(text = label, fontSize = 12.sp, color = Gray500)
        }
    }
}

@Composable
private fun ModernInfoRow(
    icon: ImageVector, label: String, value: String, iconBackground: Color, iconTint: Color
) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).background(iconBackground, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 12.sp, color = Gray500)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Gray800)
        }
    }
}

@Composable
private fun ModernReviewItem(review: Review) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val fecha = dateFormat.format(Date(review.timestamp))

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(44.dp).background(Brush.linearGradient(listOf(Amber100, Amber50)), CircleShape).border(2.dp, Amber100, CircleShape), contentAlignment = Alignment.Center) {
                    Text(text = review.userName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = Amber700, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.userName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Gray900)
                    Text(text = fecha, fontSize = 12.sp, color = Gray400)
                }
                Surface(color = Amber50, shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Amber500, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${review.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Amber700)
                    }
                }
            }
            if (review.comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(text = review.comment, fontSize = 14.sp, color = Gray700, lineHeight = 22.sp)
            }
            if (!review.response.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(color = Color(0xFFF8F9FA), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp)) {
                        Box(modifier = Modifier.width(3.dp).height(40.dp).background(Amber500, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Store, null, tint = Gray600, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Respuesta del negocio", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gray700)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = review.response, fontSize = 13.sp, color = Gray600, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernReviewDialog(onDismiss: () -> Unit, onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(64.dp).background(Amber50, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.RateReview, null, tint = Amber600, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("¿Cómo fue tu experiencia?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Gray900)
                Text("Tu opinión ayuda a otros usuarios", fontSize = 14.sp, color = Gray500)
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        val isSelected = i <= rating
                        Box(modifier = Modifier.size(48.dp).background(if (isSelected) Amber100 else Gray100, CircleShape).clickable { rating = i }, contentAlignment = Alignment.Center) {
                            Icon(imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarBorder, contentDescription = null, tint = if (isSelected) Amber500 else Gray400, modifier = Modifier.size(28.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (rating) { 1 -> "Muy malo"; 2 -> "Malo"; 3 -> "Regular"; 4 -> "Bueno"; 5 -> "Excelente"; else -> "" },
                    fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Amber700
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = comment, onValueChange = { comment = it }, label = { Text("Cuéntanos más (opcional)") }, placeholder = { Text("¿Qué te gustó o qué podría mejorar?") },
                    modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber500, focusedLabelColor = Amber600, unfocusedBorderColor = Gray200, cursorColor = Amber500), maxLines = 4
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Gray700)) { Text("Cancelar", fontWeight = FontWeight.Medium) }
                    Button(onClick = { onSubmit(rating, comment) }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Amber500)) { Text("Publicar", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
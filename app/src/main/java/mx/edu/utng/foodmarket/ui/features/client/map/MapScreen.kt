package mx.edu.utng.foodmarket.ui.features.client.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import mx.edu.utng.foodmarket.data.model.NegocioApp
import mx.edu.utng.foodmarket.ui.theme.*
import mx.edu.utng.foodmarket.ui.components.AppBottomNavigationBar // <--- IMPORTANTE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBusinessClick: (NegocioApp) -> Unit,
    onNavigate: (String) -> Unit, // <--- Recibe String para la navegación
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // === CONEXIÓN MVVM ===
    val listaNegociosReales by viewModel.negocios.collectAsState()

    // Estados Locales de UI
    var selectedBusiness by remember { mutableStateOf<NegocioApp?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val initialLocation = LatLng(21.1561, -100.9319)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 14f)
    }

    // Configuración del mapa
    val mapProperties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                isTrafficEnabled = false,
                // Si tienes el archivo JSON de estilo, descomenta esto:
                // mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, mx.edu.utng.foodmarket.R.raw.map_style)
            )
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            getCurrentLocation(context) { location ->
                scope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, 16f))
                }
            }
        }
    }

    val searchBarElevation by animateDpAsState(
        targetValue = if (isSearchActive) 16.dp else 8.dp,
        animationSpec = tween(300),
        label = "searchElevation"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. EL MAPA (FONDO)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = true
            ),
            onMapClick = {
                selectedBusiness = null
                isSearchActive = false
            }
        ) {
            listaNegociosReales.forEach { negocio ->
                Marker(
                    state = MarkerState(position = LatLng(negocio.latitud, negocio.longitud)),
                    title = negocio.nombreNegocio,
                    onClick = {
                        selectedBusiness = negocio
                        false
                    }
                )
            }
        }

        // 2. BARRA DE BÚSQUEDA (SUPERIOR)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = searchBarElevation,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Gray900.copy(alpha = 0.1f),
                        spotColor = Gray900.copy(alpha = 0.15f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (isSearchActive) Amber100 else Gray100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Buscar",
                            tint = if (isSearchActive) Amber600 else Gray500,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("¿Qué estás buscando?", color = Gray400, fontSize = 15.sp) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            },
                            modifier = Modifier.size(36.dp).background(Gray100, CircleShape)
                        ) {
                            Icon(Icons.Default.Close, "Cerrar", tint = Gray600, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Text(
                            "Buscar negocios cercanos...",
                            color = Gray500,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f).clickable { isSearchActive = true }
                        )

                        if (listaNegociosReales.isNotEmpty()) {
                            Surface(color = Amber500, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    text = "${listaNegociosReales.size}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. CONTROLES FLOTANTES (ZOOM)
        Column(
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingActionButton(
                onClick = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomIn()) } },
                modifier = Modifier.size(44.dp),
                containerColor = Color.White, contentColor = Gray700,
                shape = RoundedCornerShape(12.dp)
            ) { Icon(Icons.Default.Add, "Acercar", Modifier.size(20.dp)) }

            FloatingActionButton(
                onClick = { scope.launch { cameraPositionState.animate(CameraUpdateFactory.zoomOut()) } },
                modifier = Modifier.size(44.dp),
                containerColor = Color.White, contentColor = Gray700,
                shape = RoundedCornerShape(12.dp)
            ) { Icon(Icons.Default.Remove, "Alejar", Modifier.size(20.dp)) }
        }

        // 4. BOTÓN DE MI UBICACIÓN
        // Nota: Le puse padding bottom 130dp para que no choque con la barra flotante
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 130.dp, end = 16.dp)) {
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        getCurrentLocation(context) { loc ->
                            scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(loc, 17f)) }
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                containerColor = Color.White, contentColor = Amber600,
                shape = CircleShape,
                modifier = Modifier.size(56.dp).border(2.dp, Amber500.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.MyLocation, "Mi ubicación", Modifier.size(24.dp))
            }
        }

        // 5. TARJETA FLOTANTE DEL NEGOCIO SELECCIONADO
        AnimatedVisibility(
            visible = selectedBusiness != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (selectedBusiness != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 110.dp) // <--- Padding alto para flotar sobre la barra de navegación
                        .shadow(20.dp, RoundedCornerShape(24.dp))
                        .clickable { onBusinessClick(selectedBusiness!!) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Brush.horizontalGradient(listOf(Amber500, Amber600))))

                        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Amber50)
                                    .border(2.dp, Amber100, RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (selectedBusiness!!.categoria.lowercase()) {
                                        "restaurante" -> Icons.Outlined.Restaurant
                                        "cafetería" -> Icons.Outlined.LocalCafe
                                        "panadería" -> Icons.Outlined.BakeryDining
                                        "tienda" -> Icons.Outlined.ShoppingBag
                                        "servicios" -> Icons.Outlined.Build
                                        else -> Icons.Default.Store
                                    },
                                    contentDescription = null, tint = Amber700, modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(color = Amber100, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = selectedBusiness!!.categoria.uppercase(),
                                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Amber800,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(selectedBusiness!!.nombreNegocio, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Gray900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.LocationOn, null, tint = Gray400, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(selectedBusiness!!.direccion.ifBlank { "Sin dirección" }, color = Gray500, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            IconButton(
                                onClick = { selectedBusiness = null },
                                modifier = Modifier.size(40.dp).background(Gray100, CircleShape)
                            ) {
                                Icon(Icons.Default.Close, "Cerrar", tint = Gray500, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // 6. BARRA DE NAVEGACIÓN (FLOTANTE)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AppBottomNavigationBar(
                currentRoute = "map_screen",
                onNavigate = onNavigate
            )
        }
    }
}

// === COMPONENTE CHIP FILTRO (Local) ===
@Composable
private fun QuickFilterChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color) {
    Surface(
        color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 4.dp,
        modifier = Modifier.clickable { }
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(28.dp).background(color.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray700)
        }
    }
}

private fun getCurrentLocation(context: Context, onLocationReceived: (LatLng) -> Unit) {
    try {
        LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener { location ->
            location?.let { onLocationReceived(LatLng(it.latitude, it.longitude)) }
        }
    } catch (e: SecurityException) { }
}
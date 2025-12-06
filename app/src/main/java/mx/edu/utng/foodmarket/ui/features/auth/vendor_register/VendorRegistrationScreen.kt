package mx.edu.utng.foodmarket.ui.features.auth.vendor_register

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import mx.edu.utng.foodmarket.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorRegistrationScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: VendorRegisterViewModel = hiltViewModel()
) {
    // === ESTADOS DEL VIEWMODEL ===
    val nombreNegocio by viewModel.nombreNegocio.collectAsState()
    val nombrePropietario by viewModel.nombrePropietario.collectAsState()
    val email by viewModel.email.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val tipoNegocio by viewModel.tipoNegocio.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // === ESTADOS LOCALES UI ===
    var expandedDropdown by remember { mutableStateOf(false) }
    var showMapDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val tiposNegocio = listOf("Restaurante", "Cafetería", "Panadería", "Tienda", "Servicios", "Otro")

    // Evento Éxito
    LaunchedEffect(Unit) {
        viewModel.registerEvent.collect { onRegisterSuccess() }
    }

    // Cálculo de Progreso
    val fields = listOf(nombreNegocio, nombrePropietario, email, telefono, direccion, tipoNegocio, password, confirmPassword)
    val filledCount = fields.count { it.isNotBlank() } + if(selectedLocation != null) 1 else 0
    val totalFields = 9
    val progress = filledCount / totalFields.toFloat()

    // Color Animado del Icono
    val iconColor by animateColorAsState(
        targetValue = if (filledCount == totalFields) Green600 else Gray700,
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Gray700, Gray200, Color.White),
                    startY = 0f, endY = 800f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .shadow(4.dp, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Gray700)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Header Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(16.dp, CircleShape)
                        .background(iconColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (filledCount == totalFields) Icons.Default.CheckCircle else Icons.Default.Store,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Registra tu Negocio", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Gray900)
                Text("Únete a la red de comercio local", fontSize = 16.sp, color = Gray600, modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de Progreso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Progreso del registro", fontSize = 14.sp, color = Gray700)
                            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = if(progress == 1f) Green600 else Gray700)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = if(progress == 1f) Green600 else Gray700,
                            trackColor = Gray200
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // === FORMULARIO ===
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(20.dp, RoundedCornerShape(24.dp), ambientColor = Gray700.copy(0.1f)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        // SECCIÓN 1: DATOS DEL NEGOCIO
                        VendorSectionHeader(Icons.Outlined.Store, "Datos del Negocio")
                        Spacer(modifier = Modifier.height(16.dp))

                        VendorTextField(nombreNegocio, { viewModel.nombreNegocio.value = it }, "Nombre del Negocio", Icons.Outlined.Badge)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Dropdown Categoría
                        ExposedDropdownMenuBox(
                            expanded = expandedDropdown,
                            onExpandedChange = { expandedDropdown = !expandedDropdown }
                        ) {
                            VendorTextField(
                                value = tipoNegocio,
                                onValueChange = {},
                                label = "Categoría",
                                icon = Icons.Outlined.Category,
                                readOnly = true,
                                modifier = Modifier.menuAnchor(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDropdown,
                                onDismissRequest = { expandedDropdown = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                tiposNegocio.forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text(tipo) },
                                        onClick = {
                                            viewModel.tipoNegocio.value = tipo
                                            expandedDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        VendorTextField(descripcion, { viewModel.descripcion.value = it }, "Descripción breve", Icons.Outlined.Description)

                        Spacer(modifier = Modifier.height(24.dp))

                        // SECCIÓN 2: UBICACIÓN
                        VendorSectionHeader(Icons.Outlined.Place, "Ubicación")
                        Spacer(modifier = Modifier.height(16.dp))

                        VendorTextField(direccion, { viewModel.direccion.value = it }, "Dirección física", Icons.Outlined.Signpost)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Botón Mapa Estilizado
                        OutlinedButton(
                            onClick = { showMapDialog = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedLocation != null) Green600 else Gray400),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selectedLocation != null) Green600.copy(alpha = 0.05f) else Color.Transparent
                            )
                        ) {
                            Icon(
                                if (selectedLocation != null) Icons.Default.CheckCircle else Icons.Default.Map,
                                contentDescription = null,
                                tint = if (selectedLocation != null) Green600 else Gray600
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                if (selectedLocation != null) "Ubicación Confirmada" else "Seleccionar en Mapa",
                                color = if (selectedLocation != null) Green600 else Gray700,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // SECCIÓN 3: CONTACTO
                        VendorSectionHeader(Icons.Outlined.Person, "Contacto Propietario")
                        Spacer(modifier = Modifier.height(16.dp))

                        VendorTextField(nombrePropietario, { viewModel.nombrePropietario.value = it }, "Nombre Propietario", Icons.Outlined.Person)
                        Spacer(modifier = Modifier.height(12.dp))
                        VendorTextField(email, { viewModel.email.value = it }, "Correo Electrónico", Icons.Outlined.Email)
                        Spacer(modifier = Modifier.height(12.dp))
                        VendorTextField(telefono, { viewModel.telefono.value = it }, "Teléfono", Icons.Outlined.Phone)

                        Spacer(modifier = Modifier.height(24.dp))

                        // SECCIÓN 4: SEGURIDAD
                        VendorSectionHeader(Icons.Outlined.Security, "Seguridad")
                        Spacer(modifier = Modifier.height(16.dp))

                        VendorPasswordField(password, { viewModel.password.value = it }, "Contraseña", passwordVisible) { passwordVisible = !passwordVisible }
                        Spacer(modifier = Modifier.height(12.dp))
                        VendorPasswordField(confirmPassword, { viewModel.confirmPassword.value = it }, "Confirmar Contraseña", confirmPasswordVisible) { confirmPasswordVisible = !confirmPasswordVisible }

                        // Validacion Password
                        if (password.isNotBlank() && confirmPassword.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (password == confirmPassword) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    null,
                                    tint = if (password == confirmPassword) Green600 else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (password == confirmPassword) "Coinciden" else "No coinciden",
                                    fontSize = 12.sp,
                                    color = if (password == confirmPassword) Green600 else MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // Error Message
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.3f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(Modifier.width(8.dp))
                                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Botón Registro
                        Button(
                            onClick = { viewModel.register() },
                            modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Gray800),
                            enabled = !isLoading
                        ) {
                            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else {
                                Text("Registrar Negocio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.Storefront, null)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // === DIÁLOGO DEL MAPA ===
    if (showMapDialog) {
        Dialog(onDismissRequest = { showMapDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().height(500.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(LatLng(21.1561, -100.9319), 14f)
                        },
                        uiSettings = MapUiSettings(zoomControlsEnabled = true),
                        onMapClick = { viewModel.setLocation(it) }
                    ) {
                        if (selectedLocation != null) {
                            Marker(state = MarkerState(position = selectedLocation!!), title = "Ubicación del Negocio")
                        }
                    }
                    // Botón flotante confirmar
                    Button(
                        onClick = { showMapDialog = false },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = if(selectedLocation != null) Green600 else Gray600)
                    ) {
                        Text(if (selectedLocation == null) "Toca el mapa para ubicar" else "Confirmar Ubicación")
                    }
                }
            }
        }
    }
}

// === COMPONENTES LOCALES ESTILO VENDEDOR (Gris/Profesional) ===

@Composable
private fun VendorSectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(32.dp).background(Gray200, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Gray800, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, fontWeight = FontWeight.SemiBold, color = Gray800, fontSize = 16.sp)
    }
}

@Composable
private fun VendorTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = if(value.isNotBlank()) Gray800 else Gray400) },
        trailingIcon = trailingIcon,
        readOnly = readOnly,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gray600,
            focusedLabelColor = Gray800,
            unfocusedBorderColor = Gray200,
            cursorColor = Gray800,
            focusedContainerColor = Gray100.copy(0.5f),
            unfocusedContainerColor = Gray50
        )
    )
}

@Composable
private fun VendorPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = if(value.isNotBlank()) Gray800 else Gray400) },
        trailingIcon = { IconButton(onClick = onToggle) { Icon(if (isVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null, tint = Gray500) } },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gray600,
            focusedLabelColor = Gray800,
            unfocusedBorderColor = Gray200,
            cursorColor = Gray800,
            focusedContainerColor = Gray100.copy(0.5f),
            unfocusedContainerColor = Gray50
        )
    )
}
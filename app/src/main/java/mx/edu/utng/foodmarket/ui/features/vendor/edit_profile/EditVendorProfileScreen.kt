package mx.edu.utng.foodmarket.ui.features.vendor.edit_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mx.edu.utng.foodmarket.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditVendorProfileScreen(
    onBack: () -> Unit,
    viewModel: EditVendorProfileViewModel = hiltViewModel()
) {
    // Estados ViewModel
    val businessName by viewModel.businessName.collectAsState()
    val description by viewModel.description.collectAsState()
    val address by viewModel.address.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val ownerName by viewModel.ownerName.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Snackbar Control
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("Datos actualizados correctamente")
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent // Importante para ver el fondo del Box
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Gray800, Gray900, Gray200),
                        startY = 0f, endY = 1000f
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Top Bar Transparente
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Gray800)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Ícono Animado
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .shadow(16.dp, CircleShape)
                                .background(Gray800, CircleShape)
                                .border(2.dp, Color.White.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Editar Negocio",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Mantén tu información actualizada",
                            fontSize = 16.sp,
                            color = Color.White.copy(0.7f),
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Tarjeta de Formulario
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(20.dp, RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                // Sección 1: Datos Negocio
                                VendorEditHeader(Icons.Outlined.Store, "Información del Negocio")
                                Spacer(modifier = Modifier.height(16.dp))

                                VendorEditField(businessName, { viewModel.businessName.value = it }, "Nombre del Negocio", Icons.Outlined.Badge)
                                Spacer(modifier = Modifier.height(12.dp))
                                VendorEditField(description, { viewModel.description.value = it }, "Descripción", Icons.Outlined.Description, maxLines = 3)

                                Spacer(modifier = Modifier.height(24.dp))

                                // Sección 2: Ubicación
                                VendorEditHeader(Icons.Outlined.Place, "Ubicación y Contacto")
                                Spacer(modifier = Modifier.height(16.dp))

                                VendorEditField(address, { viewModel.address.value = it }, "Dirección", Icons.Outlined.LocationOn, maxLines = 2)
                                Spacer(modifier = Modifier.height(12.dp))
                                VendorEditField(phone, { viewModel.phone.value = it }, "Teléfono del Negocio", Icons.Outlined.Phone)

                                Spacer(modifier = Modifier.height(24.dp))

                                // Sección 3: Datos Propietario
                                VendorEditHeader(Icons.Outlined.Person, "Datos del Propietario")
                                Spacer(modifier = Modifier.height(16.dp))

                                VendorEditField(ownerName, { viewModel.ownerName.value = it }, "Tu Nombre", Icons.Outlined.Person)
                                Text(
                                    text = "Visible al responder reseñas.",
                                    fontSize = 12.sp,
                                    color = Gray500,
                                    modifier = Modifier.align(Alignment.Start).padding(top = 4.dp, start = 4.dp)
                                )

                                // Mensaje de Error
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

                                Spacer(modifier = Modifier.height(32.dp))

                                // Botón Guardar
                                Button(
                                    onClick = { viewModel.guardarCambios() },
                                    enabled = !isSaving,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Gray900,
                                        disabledContainerColor = Gray400
                                    )
                                ) {
                                    if (isSaving) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                    } else {
                                        Text("Guardar Todo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Default.Save, null)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

// Componentes Reutilizables Estilo Vendedor

@Composable
private fun VendorEditHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.size(32.dp).background(Gray100, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Gray800, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Gray800, fontSize = 16.sp)
    }
}

@Composable
private fun VendorEditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (value.isNotBlank()) Gray800 else Gray400
            )
        },
        modifier = Modifier.fillMaxWidth(),
        maxLines = maxLines,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Gray600,
            focusedLabelColor = Gray800,
            unfocusedBorderColor = Gray200,
            cursorColor = Gray800,
            focusedContainerColor = Gray100.copy(alpha = 0.5f),
            unfocusedContainerColor = Gray50
        )
    )
}
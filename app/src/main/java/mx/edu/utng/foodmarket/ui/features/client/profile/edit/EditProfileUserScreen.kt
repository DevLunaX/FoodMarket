package mx.edu.utng.foodmarket.ui.features.client.profile.edit

import androidx.compose.foundation.background
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
fun EditProfileUserScreen(
    onBack: () -> Unit,
    onUpdateSuccess: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    // Estados del ViewModel
    val username by viewModel.username.collectAsState()
    val nombre by viewModel.nombre.collectAsState()
    val telefono by viewModel.telefono.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Escuchar éxito
    LaunchedEffect(Unit) {
        viewModel.updateEvent.collect {
            onUpdateSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Amber500, Amber100, Color.White),
                    startY = 0f, endY = 600f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Gray700)
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
                            .background(Amber100, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Amber600
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Editar Perfil",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900
                    )
                    Text(
                        text = "Actualiza tu información personal",
                        fontSize = 16.sp,
                        color = Gray600,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Tarjeta de Formulario
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(20.dp, RoundedCornerShape(24.dp), ambientColor = Amber500.copy(0.1f)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ModernEditField(
                                value = username,
                                onValueChange = { viewModel.username.value = it },
                                label = "Nombre de Usuario",
                                icon = Icons.Outlined.AccountCircle
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ModernEditField(
                                value = nombre,
                                onValueChange = { viewModel.nombre.value = it },
                                label = "Nombre Completo",
                                icon = Icons.Outlined.Person
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ModernEditField(
                                value = telefono,
                                onValueChange = { viewModel.telefono.value = it },
                                label = "Teléfono",
                                icon = Icons.Outlined.Phone
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
                                    containerColor = Amber500,
                                    disabledContainerColor = Amber500.copy(0.6f)
                                )
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Guardar Cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

// Componente reutilizable para campos de texto
@Composable
private fun ModernEditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (value.isNotBlank()) Amber600 else Gray400
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Amber500,
            focusedLabelColor = Amber600,
            unfocusedBorderColor = Gray200,
            cursorColor = Amber500,
            focusedContainerColor = Amber50.copy(alpha = 0.3f),
            unfocusedContainerColor = Gray50
        )
    )
}
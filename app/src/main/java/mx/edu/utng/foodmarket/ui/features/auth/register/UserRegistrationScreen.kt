package mx.edu.utng.foodmarket.ui.features.auth.register

import androidx.compose.animation.animateColorAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import mx.edu.utng.foodmarket.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistrationScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    // INYECTAMOS EL VIEWMODEL
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // === VARIABLES DE ESTADO LOCAL (Formulario) ===
    var username by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados que vienen del ViewModel
    var showError by remember { mutableStateOf(false) }
    var localErrorMessage by remember { mutableStateOf("") } // Error de validación local

    val isLoading by viewModel.isLoading.collectAsState()
    val vmErrorMessage by viewModel.errorMessage.collectAsState()

    // Escuchar éxito del registro
    LaunchedEffect(Unit) {
        viewModel.registerEvent.collect {
            onRegisterSuccess()
        }
    }

    // Calcular progreso del formulario
    val filledFields = listOf(username, nombre, apellido, email, telefono, password, confirmPassword)
        .count { it.isNotBlank() }
    val progress = filledFields / 7f

    // Color animado
    val iconBackgroundColor by animateColorAsState(
        targetValue = when {
            filledFields == 7 -> Green600
            filledFields > 0 -> Amber500
            else -> Amber100
        },
        label = "iconBgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Amber500, Amber100, Color.White),
                    startY = 0f,
                    endY = 600f
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Gray700
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Ícono animado
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(16.dp, CircleShape)
                        .background(iconBackgroundColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (filledFields == 7) Icons.Default.CheckCircle else Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = if (filledFields > 0) Color.White else Amber600
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Crea tu cuenta",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )

                Text(
                    text = "Completa los datos para registrarte",
                    fontSize = 16.sp,
                    color = Gray600,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Indicador de progreso
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Progreso",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Gray700
                            )
                            Text(
                                text = "$filledFields de 7 campos",
                                fontSize = 14.sp,
                                color = if (filledFields == 7) Green600 else Amber600,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (filledFields == 7) Green600 else Amber500,
                            trackColor = Gray200
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Card formulario
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Amber500.copy(alpha = 0.1f),
                            spotColor = Amber500.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SectionHeader(Icons.Outlined.AccountCircle, "Información de cuenta")
                        Spacer(modifier = Modifier.height(16.dp))

                        ModernTextField(username, { username = it }, "Nombre de Usuario", Icons.Outlined.AlternateEmail)
                        Spacer(modifier = Modifier.height(16.dp))
                        ModernTextField(email, { email = it }, "Correo Electrónico", Icons.Outlined.Email)
                        Spacer(modifier = Modifier.height(24.dp))

                        SectionHeader(Icons.Outlined.Person, "Datos personales")
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ModernTextField(nombre, { nombre = it }, "Nombre", Icons.Outlined.Badge, Modifier.weight(1f))
                            ModernTextField(apellido, { apellido = it }, "Apellido", Icons.Outlined.Badge, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ModernTextField(telefono, { telefono = it }, "Teléfono", Icons.Outlined.Phone)
                        Spacer(modifier = Modifier.height(24.dp))

                        SectionHeader(Icons.Outlined.Security, "Seguridad")
                        Spacer(modifier = Modifier.height(16.dp))

                        ModernPasswordField(password, { password = it }, "Contraseña", passwordVisible) { passwordVisible = !passwordVisible }
                        Spacer(modifier = Modifier.height(16.dp))
                        ModernPasswordField(confirmPassword, { confirmPassword = it }, "Confirmar Contraseña", confirmPasswordVisible) { confirmPasswordVisible = !confirmPasswordVisible }

                        if (password.isNotBlank() && confirmPassword.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Icon(
                                    imageVector = if (password == confirmPassword) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (password == confirmPassword) Green600 else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (password == confirmPassword) "Las contraseñas coinciden" else "Las contraseñas no coinciden",
                                    fontSize = 12.sp,
                                    color = if (password == confirmPassword) Green600 else MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // Mostrar error local o del ViewModel
                        if (showError || vmErrorMessage != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = vmErrorMessage ?: localErrorMessage,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Botón de Registro
                        Button(
                            enabled = !isLoading,
                            onClick = {
                                // 1. Validaciones Locales
                                when {
                                    username.isBlank() || nombre.isBlank() || apellido.isBlank() || email.isBlank() ||
                                            telefono.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                        showError = true
                                        localErrorMessage = "Todos los campos son obligatorios"
                                    }
                                    password != confirmPassword -> {
                                        showError = true
                                        localErrorMessage = "Las contraseñas no coinciden"
                                    }
                                    password.length < 6 -> {
                                        showError = true
                                        localErrorMessage = "La contraseña debe tener al menos 6 caracteres"
                                    }
                                    else -> {
                                        showError = false
                                        // 2. Llamada al ViewModel
                                        viewModel.register(
                                            username = username,
                                            nombre = nombre,
                                            apellido = apellido,
                                            email = email,
                                            telefono = telefono,
                                            pass = password
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Amber500,
                                disabledContainerColor = Amber500.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = if (!isLoading) 8.dp else 0.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Amber500,
                                    spotColor = Amber600
                                )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    Text("Crear cuenta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.HowToReg, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Link Login
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Amber50)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Ya tienes cuenta? ", color = Gray700, fontSize = 15.sp)
                        TextButton(onClick = onBack) { Text("Inicia sesión", color = Amber700, fontWeight = FontWeight.Bold, fontSize = 15.sp) }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// === COMPONENTES REUTILIZABLES (Sin Cambios) ===
@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).background(Amber100, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = Amber700, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Gray800)
    }
}

@Composable
private fun ModernTextField(value: String, onValueChange: (String) -> Unit, label: String, leadingIcon: ImageVector, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, null, tint = if (value.isNotBlank()) Amber600 else Gray400) },
        modifier = modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber500, focusedLabelColor = Amber600, unfocusedBorderColor = Gray200, cursorColor = Amber500, focusedContainerColor = Amber50.copy(alpha = 0.3f), unfocusedContainerColor = Gray50)
    )
}

@Composable
private fun ModernPasswordField(value: String, onValueChange: (String) -> Unit, label: String, passwordVisible: Boolean, onToggleVisibility: () -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = if (value.isNotBlank()) Amber600 else Gray400) },
        trailingIcon = { IconButton(onClick = onToggleVisibility) { Icon(if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, if (passwordVisible) "Ocultar" else "Mostrar", tint = Gray500) } },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber500, focusedLabelColor = Amber600, unfocusedBorderColor = Gray200, cursorColor = Amber500, focusedContainerColor = Amber50.copy(alpha = 0.3f), unfocusedContainerColor = Gray50)
    )
}
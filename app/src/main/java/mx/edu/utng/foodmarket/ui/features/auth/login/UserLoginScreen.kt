package mx.edu.utng.foodmarket.ui.features.auth.login // Ajusta el paquete

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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel // IMPORTANTE: Librería de Hilt
import mx.edu.utng.foodmarket.ui.features.welcome.* // Para tus colores (Amber500, etc)
import mx.edu.utng.foodmarket.ui.theme.Gray100
import mx.edu.utng.foodmarket.ui.theme.Gray200
import mx.edu.utng.foodmarket.ui.theme.Gray400
import mx.edu.utng.foodmarket.ui.theme.Gray50
import mx.edu.utng.foodmarket.ui.theme.Gray500


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    // INYECCIÓN DE DEPENDENCIA: Aquí entra el cerebro automáticamente
    viewModel: LoginViewModel = hiltViewModel()
) {
    // 1. OBSERVAMOS EL ESTADO DEL VIEWMODEL
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()

    // 2. ESCUCHAMOS EVENTOS DE NAVEGACIÓN (Éxito en login)
    LaunchedEffect(Unit) {
        viewModel.loginEvent.collect { rol ->
            onLoginSuccess(rol)
        }
    }

    // Color animado para el ícono principal
    val iconBackgroundColor by animateColorAsState(
        targetValue = if (email.isNotBlank() || password.isNotBlank()) Amber500 else Amber100,
        label = "iconBgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Amber500, Amber100, Color.White),
                    startY = 0f,
                    endY = 800f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar transparente
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
                Spacer(modifier = Modifier.height(24.dp))

                // Ícono animado
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(16.dp, CircleShape)
                        .background(iconBackgroundColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = if (email.isNotBlank() || password.isNotBlank()) Color.White else Amber600
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Textos
                Text(
                    text = "¡Bienvenido de nuevo!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray800
                )

                Text(
                    text = "Inicia sesión para continuar",
                    fontSize = 16.sp,
                    color = Gray600,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Formulario
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
                        // Campo Email (Conectado al ViewModel)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.onEmailChange(it) }, // <-- Evento al ViewModel
                            label = { Text("Correo Electrónico") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = if (email.isNotBlank()) Amber600 else Gray200
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Amber500,
                                focusedLabelColor = Amber600,
                                unfocusedBorderColor = Gray100,
                                cursorColor = Amber500,
                                focusedContainerColor = Amber50.copy(alpha = 0.3f),
                                unfocusedContainerColor = Gray50
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Campo Contraseña (Conectado al ViewModel)
                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.onPasswordChange(it) }, // <-- Evento
                            label = { Text("Contraseña") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = if (password.isNotBlank()) Amber600 else Gray400
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.togglePasswordVisibility() }) { // <-- Evento
                                    Icon(
                                        if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                        tint = Gray500
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Amber500,
                                focusedLabelColor = Amber600,
                                unfocusedBorderColor = Gray200,
                                cursorColor = Amber500,
                                focusedContainerColor = Amber50.copy(alpha = 0.3f),
                                unfocusedContainerColor = Gray50
                            )
                        )

                        // Mensaje de error (Observando el estado)
                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage!!,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { /* TODO: Navegar a recuperar contraseña */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                "¿Olvidaste tu contraseña? ",
                                color = Amber700,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón Iniciar Sesión
                        Button(
                            enabled = !isLoading,
                            onClick = { viewModel.login() }, // <-- La lógica se fue al ViewModel
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
                                ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Iniciar Sesión",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Separador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Gray200
                    )
                    Text(text = "  o  ", color = Gray500, fontSize = 14.sp)
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Gray200
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Registro
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Amber50)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿No tienes cuenta?", color = Gray700, fontSize = 15.sp)
                        TextButton(onClick = onNavigateToRegister) {
                            Text(
                                "Regístrate aquí",
                                color = Amber700,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
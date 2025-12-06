package mx.edu.utng.foodmarket.ui.features.auth.vendor_login

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import mx.edu.utng.foodmarket.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorLoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    // INYECTAMOS EL VIEWMODEL
    viewModel: VendorLoginViewModel = hiltViewModel()
) {
    // 1. OBSERVAR ESTADOS
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // 2. ESCUCHAR EVENTOS
    LaunchedEffect(Unit) {
        viewModel.loginEvent.collect { rol ->
            onLoginSuccess(rol)
        }
    }

    // Color animado (Mantenido de tu diseño)
    val iconBackgroundColor by animateColorAsState(
        targetValue = if (email.isNotBlank() || password.isNotBlank()) Gray700 else Gray200,
        label = "iconBgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Gray700, Gray200, Color.White),
                    startY = 0f,
                    endY = 800f
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
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = if (email.isNotBlank() || password.isNotBlank()) Color.White else Gray600
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Badge de vendedor
                Surface(
                    color = Gray700,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PANEL VENDEDOR",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Acceso Vendedor",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )

                Text(
                    text = "Administra tu negocio local",
                    fontSize = 16.sp,
                    color = Gray600,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Card formulario
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = Gray700.copy(alpha = 0.1f),
                            spotColor = Gray700.copy(alpha = 0.2f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = if (email.isNotBlank()) Gray700 else Gray400
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Gray600,
                                focusedLabelColor = Gray700,
                                unfocusedBorderColor = Gray200,
                                cursorColor = Gray600,
                                focusedContainerColor = Gray100.copy(alpha = 0.5f),
                                unfocusedContainerColor = Gray50
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            label = { Text("Contraseña") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = if (password.isNotBlank()) Gray700 else Gray400
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
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
                                focusedBorderColor = Gray600,
                                focusedLabelColor = Gray700,
                                unfocusedBorderColor = Gray200,
                                cursorColor = Gray600,
                                focusedContainerColor = Gray100.copy(alpha = 0.5f),
                                unfocusedContainerColor = Gray50
                            )
                        )

                        // Error
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
                            onClick = { /* TODO */ },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                "¿Olvidaste tu contraseña? ",
                                color = Gray700,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón
                        Button(
                            enabled = !isLoading,
                            onClick = { viewModel.login() },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gray700,
                                disabledContainerColor = Gray700.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(
                                    elevation = if (!isLoading) 8.dp else 0.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Gray700,
                                    spotColor = Gray800
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
                                        text = "Acceder al Panel",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.Login,
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
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Gray400)
                    Text(text = "  o  ", color = Gray500, fontSize = 14.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Gray400)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Registro Vendedor
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Gray100)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.AddBusiness,
                            contentDescription = null,
                            tint = Gray600,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "¿Quieres vender en Consume Local?",
                            color = Gray700,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        TextButton(onClick = onNavigateToRegister) {
                            Text(
                                "Registra tu negocio aquí",
                                color = Gray800,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Gray800,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
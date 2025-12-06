package mx.edu.utng.foodmarket.ui.features.welcome

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores modernos
val Amber50 = Color(0xFFFFFBEB)
val Amber100 = Color(0xFFFEF3C7)
val Amber400 = Color(0xFFFBBF24)
val Amber500 = Color(0xFFF59E0B)
val Amber600 = Color(0xFFD97706)
val Amber700 = Color(0xFFB45309)
val Amber800 = Color(0xFF92400E)
val Amber900 = Color(0xFF78350F)
val Gray600 = Color(0xFF4B5563)
val Gray700 = Color(0xFF374151)
val Gray800 = Color(0xFF1F2937)

@Composable
fun WelcomeScreen(
    onNavigateToUserLogin: () -> Unit,
    onNavigateToVendorLogin: () -> Unit
) {
    // Animación de entrada
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    val offsetAnim by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 50.dp,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "offset"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Amber50,
                        Amber100,
                        Color.White
                    )
                )
            )
    ) {
        // Círculos decorativos de fondo
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-50).dp)
                .clip(CircleShape)
                .background(Amber400.copy(alpha = 0.1f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = 100.dp)
                .clip(CircleShape)
                .background(Amber500.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .graphicsLayer {
                    alpha = alphaAnim
                    translationY = offsetAnim.toPx()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono principal con animación
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .size(120.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        ambientColor = Amber500.copy(alpha = 0.3f),
                        spotColor = Amber500.copy(alpha = 0.3f)
                    )
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Amber400, Amber600)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Título principal
            Text(
                text = "Consume",
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                color = Gray800,
                letterSpacing = 2.sp
            )
            Text(
                text = "Local",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Amber600,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            Text(
                text = "Descubre y apoya los mejores\nnegocios de tu comunidad",
                fontSize = 16.sp,
                color = Gray600,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Botón principal - Buscar Negocios
            Button(
                onClick = onNavigateToUserLogin,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Amber500.copy(alpha = 0.4f),
                        spotColor = Amber500.copy(alpha = 0.4f)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Amber500, Amber600)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Explorar Negocios",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón secundario - Soy Vendedor (Outlined)
            OutlinedButton(
                onClick = onNavigateToVendorLogin,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Gray700
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Gray600, Gray700)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = null,
                        tint = Gray700,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Soy Vendedor",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Texto inferior
            Text(
                text = "🌱 Impulsando la economía local",
                fontSize = 13.sp,
                color = Gray600.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onNavigateToUserLogin = {},
        onNavigateToVendorLogin = {}
    )
}
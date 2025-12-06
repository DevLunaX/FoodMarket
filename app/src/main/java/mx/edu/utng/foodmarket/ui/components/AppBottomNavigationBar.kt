package mx.edu.utng.foodmarket.ui.components // Ajusta a tu paquete

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utng.foodmarket.ui.theme.*

// Data class para configurar cada ítem limpiamente
data class BottomNavItem(
    val route: String,
    val label: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
    val color: Color,
    val containerColor: Color
)

@Composable
fun AppBottomNavigationBar(
    currentRoute: String?, // Usamos String para coincidir con tu MainActivity
    onNavigate: (String) -> Unit
) {
    // Definimos los ítems de navegación
    val items = listOf(
        BottomNavItem(
            route = "map_screen",
            label = "Explorar",
            iconSelected = Icons.Filled.Map,
            iconUnselected = Icons.Outlined.Map,
            color = Amber600,
            containerColor = Amber100
        ),
        BottomNavItem(
            route = "favorites_screen", // Asegúrate de crear esta ruta luego
            label = "Favoritos",
            iconSelected = Icons.Filled.Favorite,
            iconUnselected = Icons.Outlined.FavoriteBorder,
            color = Pink500,
            containerColor = Pink100
        ),
        BottomNavItem(
            route = "profile_screen", // Asegúrate de crear esta ruta luego
            label = "Perfil",
            iconSelected = Icons.Filled.Person,
            iconUnselected = Icons.Outlined.Person,
            color = Blue600,
            containerColor = Blue100
        )
    )

    // Contenedor Flotante
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp) // Margen para que flote
            .height(70.dp) // Altura de la barra
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(35.dp), // Bordes totalmente redondos
                    spotColor = Gray400.copy(alpha = 0.4f),
                    ambientColor = Gray400.copy(alpha = 0.4f)
                ),
            color = Color.White,
            shape = RoundedCornerShape(35.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    // Ítem Animado Personalizado
                    ModernNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                onNavigate(item.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModernNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animación de expansión
    val backgroundColor = if (isSelected) item.containerColor else Color.Transparent
    val contentColor = if (isSelected) item.color else Gray400

    Box(
        modifier = Modifier
            .height(50.dp)
            .clip(CircleShape) // Forma de píldora
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Quitamos el efecto ripple por defecto para que se vea más limpio
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp), // Padding interno del botón
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icono
            Icon(
                imageVector = if (isSelected) item.iconSelected else item.iconUnselected,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            // Texto animado (Solo aparece si está seleccionado)
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally(),
            ) {
                Text(
                    text = item.label,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
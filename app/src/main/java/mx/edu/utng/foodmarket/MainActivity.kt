package mx.edu.utng.foodmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

// --- IMPORTS DE TUS PANTALLAS ---
import mx.edu.utng.foodmarket.ui.theme.FoodMarketTheme
import mx.edu.utng.foodmarket.ui.features.welcome.WelcomeScreen
import mx.edu.utng.foodmarket.ui.features.auth.login.UserLoginScreen
import mx.edu.utng.foodmarket.ui.features.auth.register.UserRegistrationScreen
import mx.edu.utng.foodmarket.ui.features.auth.vendor_login.VendorLoginScreen
import mx.edu.utng.foodmarket.ui.features.auth.vendor_register.VendorRegistrationScreen
import mx.edu.utng.foodmarket.ui.features.client.map.MapScreen
import mx.edu.utng.foodmarket.ui.features.client.detail.BusinessDetailScreen // <--- IMPORT NUEVO
import mx.edu.utng.foodmarket.ui.features.client.favorites.FavoritesScreen
import mx.edu.utng.foodmarket.ui.features.client.profile.ProfileScreen
import mx.edu.utng.foodmarket.ui.features.client.profile.edit.EditProfileUserScreen
import mx.edu.utng.foodmarket.ui.features.vendor.dashboard.VendorPanelScreen
import mx.edu.utng.foodmarket.ui.features.vendor.edit_profile.EditVendorProfileScreen
import mx.edu.utng.foodmarket.ui.features.vendor.reviews.ViewReviewsScreen
import mx.edu.utng.foodmarket.ui.features.vendor.statistics.ViewStatisticsScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodMarketTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {

        // 1. PANTALLA DE BIENVENIDA
        composable("welcome") {
            WelcomeScreen(
                onNavigateToUserLogin = {
                    navController.navigate("login_user")
                },
                onNavigateToVendorLogin = {
                    navController.navigate("login_vendor")
                }
            )
        }

        // 2. LOGIN DE USUARIO (CLIENTE)
        composable("login_user") {
            UserLoginScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = { rol ->
                    // Si es cliente, vamos al mapa
                    if (rol == "CLIENTE") {
                        navController.navigate("map_screen") {
                            popUpTo("login_user") { inclusive = true }
                        }
                    } else {
                        navController.navigate("vendor_dashboard")
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register_user")
                }
            )
        }

        // 3. REGISTRO DE USUARIO
        composable("register_user") {
            UserRegistrationScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("login_user") {
                        popUpTo("register_user") { inclusive = true }
                    }
                }
            )
        }

        // 4. LOGIN DE VENDEDOR
        composable("login_vendor") {
            VendorLoginScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = { rol ->
                    navController.navigate("vendor_dashboard") {
                        popUpTo("login_vendor") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register_vendor")
                }
            )
        }

        // 5. REGISTRO DE VENDEDOR
        composable("register_vendor") {
            VendorRegistrationScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("login_vendor") {
                        popUpTo("register_vendor") { inclusive = true }
                    }
                }
            )
        }

        // 6. MAPA PRINCIPAL (Actualizado)
        composable("map_screen") {
            MapScreen(
                onBusinessClick = { negocio ->
                    // Al hacer click, pasamos el ID a la ruta de detalle
                    navController.navigate("business_detail/${negocio.id}")
                },
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // 7. DETALLE DEL NEGOCIO (Nueva Ruta)
        composable(
            route = "business_detail/{businessId}", // Recibimos el parámetro
            arguments = listOf(navArgument("businessId") { type = NavType.StringType })
        ) {
            BusinessDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 8. DASHBOARD VENDEDOR (Ahora sí usa la pantalla real)
        composable("vendor_dashboard") {
            VendorPanelScreen(
                onExit = {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToEditProfile = {
                    navController.navigate("edit_vendor_profile")
                },
                onNavigateToStatistics = {
                     navController.navigate("vendor_stats")
                },
                onNavigateToReviews = {
                     navController.navigate("vendor_reviews")
                }
            )
        }
        // 9. FAVORITOS
        composable("favorites_screen") {
            FavoritesScreen(
                onBusinessClick = { negocio ->
                    navController.navigate("business_detail/${negocio.id}")
                },
                onNavigate = { route ->
                    // Navegación inteligente: si vamos al mapa, borramos historial
                    if (route == "map_screen") {
                        navController.navigate(route) {
                            popUpTo("map_screen") { inclusive = true }
                        }
                    } else {
                        navController.navigate(route)
                    }
                }
            )
        }
        // 10. PERFIL
        composable("profile_screen") {
            ProfileScreen(
                onLogoutClick = {
                    // Al cerrar sesión, volvemos a la Bienvenida y limpiamos historial
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigate = { route ->
                    if (route != "profile_screen") {
                        navController.navigate(route) {
                            if (route == "map_screen") popUpTo("map_screen") { inclusive = true }
                        }
                    }
                }
            )
        }
        // 11. EDITAR PERFIL
        composable("edit_profile") {
            EditProfileUserScreen(
                onBack = { navController.popBackStack() },
                onUpdateSuccess = {
                    // Al guardar, volvemos al perfil
                    navController.popBackStack()
                }
            )
        }
        // 12. ESTADÍSTICAS VENDEDOR (Nueva Ruta)
        composable("vendor_stats") {
            ViewStatisticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 13. RESEÑAS VENDEDOR (Nueva Ruta)
        composable("vendor_reviews") {
            ViewReviewsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 14. EDITAR PERFIL VENDEDOR (Nueva Ruta)
        composable("edit_vendor_profile") {
            EditVendorProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
package com.example.carwashautospa.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carwashautospa.ui.administrador.DashboardAdminScreen
import com.example.carwashautospa.ui.auth.LoginScreen
import com.example.carwashautospa.ui.auth.RegistroScreen
import com.example.carwashautospa.ui.cliente.InicioClienteScreen
import com.example.carwashautospa.ui.operario.DashboardOperarioScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object SplashAuth : Screen("splash_auth")
    object InicioCliente : Screen("inicio_cliente")
    object DashboardOperario : Screen("dashboard_operario")
    object DashboardAdmin : Screen("dashboard_admin")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Definir pantalla de inicio según si hay sesión activa
    val startDestination = if (auth.currentUser != null) Screen.SplashAuth.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {

        // 1. LOGIN
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Screen.SplashAuth.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegistro = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }

        // 2. REGISTRO
        composable(Screen.Registro.route) {
            RegistroScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.SplashAuth.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. ¿QUIÉN ES? (Redirección por Rol)
        composable(Screen.SplashAuth.route) {
            LaunchedEffect(Unit) {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    db.collection("usuarios").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val rol = doc.getString("rol")?.uppercase() ?: "CLIENTE"
                            val destino = when (rol) {
                                "ADMINISTRADOR", "ADMIN" -> Screen.DashboardAdmin.route
                                "OPERARIO" -> Screen.DashboardOperario.route
                                else -> Screen.InicioCliente.route
                            }
                            navController.navigate(destino) {
                                popUpTo(Screen.SplashAuth.route) { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            navController.navigate(Screen.InicioCliente.route) {
                                popUpTo(Screen.SplashAuth.route) { inclusive = true }
                            }
                        }
                } else {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SplashAuth.route) { inclusive = true }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // 3. RUTAS SEGÚN ROL
        composable(Screen.InicioCliente.route) {
            InicioClienteScreen(
                onCerrarSesion = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DashboardOperario.route) {
            DashboardOperarioScreen(
                onCerrarSesion = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.DashboardAdmin.route) {
            DashboardAdminScreen(
                onCerrarSesion = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToUsuarios = { /* Navegar a Usuarios */ },
                onNavigateToServicios = { /* Navegar a Servicios */ },
                onNavigateToHorarios = { /* Navegar a Horarios */ },
                onNavigateToReservas = { /* Navegar a Reservas */ },
                onNavigateToReportes = { /* Navegar a Reportes */ }
            )
        }
    }
}
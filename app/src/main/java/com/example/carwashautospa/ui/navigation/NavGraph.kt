package com.example.carwashautospa.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.*
import com.example.carwashautospa.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carwashautospa.data.model.Horario
import com.example.carwashautospa.ui.administrador.DashboardAdminScreen
import com.example.carwashautospa.ui.auth.LoginScreen
import com.example.carwashautospa.ui.auth.RegistroScreen
import com.example.carwashautospa.ui.cliente.InicioClienteScreen
import com.example.carwashautospa.ui.cliente.reserva.*
import com.example.carwashautospa.ui.cliente.servicios.ServiciosScreen
import com.example.carwashautospa.ui.cliente.vehiculos.*
import com.example.carwashautospa.ui.operario.DashboardOperarioScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")
    object SplashAuth : Screen("splash_auth")
    object InicioCliente : Screen("inicio_cliente")
    object MisVehiculos : Screen("mis_vehiculos")
    object AgregarVehiculo : Screen("agregar_vehiculo")
    object EditarVehiculo : Screen("editar_vehiculo/{vehiculoId}")
    object ServiciosCliente : Screen("servicios_cliente")
    object SeleccionarVehiculo : Screen("seleccionar_vehiculo/{servicioId}/{servicioNombre}")
    object DashboardOperario : Screen("dashboard_operario")
    object DashboardAdmin : Screen("dashboard_admin")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // El Splash siempre será la pantalla de inicio
    val startDestination = Screen.SplashAuth.route

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

        // 2. ¿QUIÉN ES? (Splash Screen con Animación)
        composable(Screen.SplashAuth.route) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.carwash_anim))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            LaunchedEffect(Unit) {
                // Tiempo mínimo de visualización (3 segundos)
                delay(3000)

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

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(250.dp)
                )
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
                },
                onNavigateToVehiculos = { navController.navigate(Screen.MisVehiculos.route) },
                onNavigateToReservar = { navController.navigate(Screen.ServiciosCliente.route) }
            )
        }

        // --- MÓDULO VEHÍCULOS ---
        composable(Screen.MisVehiculos.route) {
            VehiculosScreen(
                onNavigateToAgregar = { navController.navigate(Screen.AgregarVehiculo.route) },
                onNavigateToEditar = { id -> navController.navigate("editar_vehiculo/$id") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AgregarVehiculo.route) {
            AgregarVehiculoScreen(
                onVehiculoAgregado = { navController.popBackStack() }
            )
        }

        composable(Screen.EditarVehiculo.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            EditarVehiculoScreen(
                vehiculoId = id,
                onVehiculoEditado = { navController.popBackStack() }
            )
        }

        // --- MÓDULO RESERVAS ---
        composable(Screen.ServiciosCliente.route) {
            ServiciosScreen(
                onServicioSeleccionado = { id, nombre ->
                    navController.navigate("seleccionar_vehiculo/$id/$nombre")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeleccionarVehiculo.route) { backStackEntry ->
            val sId = backStackEntry.arguments?.getString("servicioId") ?: ""
            val sNom = backStackEntry.arguments?.getString("servicioNombre") ?: ""
            SeleccionarVehiculoScreen(
                onVehiculoSeleccionado = { vId ->
                    navController.navigate("seleccionar_fecha/$sId/$sNom/$vId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("seleccionar_fecha/{servicioId}/{servicioNombre}/{vehiculoId}") { backStackEntry ->
            val sId = backStackEntry.arguments?.getString("servicioId") ?: ""
            val sNom = backStackEntry.arguments?.getString("servicioNombre") ?: ""
            val vId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            SeleccionarFechaScreen(
                onFechaSeleccionada = { fecha ->
                    val fechaLimpia = fecha.replace("/", "-")
                    navController.navigate("seleccionar_horario/$sId/$sNom/$vId/$fechaLimpia")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("seleccionar_horario/{servicioId}/{servicioNombre}/{vehiculoId}/{fecha}") { backStackEntry ->
            val sId = backStackEntry.arguments?.getString("servicioId") ?: ""
            val sNom = backStackEntry.arguments?.getString("servicioNombre") ?: ""
            val vId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val fecha = backStackEntry.arguments?.getString("fecha")?.replace("-", "/") ?: ""
            
            SeleccionarHorarioScreen(
                fecha = fecha,
                onHorarioSeleccionado = { horario ->
                    val fechaLimpia = fecha.replace("/", "-")
                    navController.navigate("confirmar_reserva/$sId/$sNom/$vId/$fechaLimpia/${horario.id}/${horario.horaInicio}/${horario.capacidad}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("confirmar_reserva/{servicioId}/{servicioNombre}/{vehiculoId}/{fecha}/{horarioId}/{horaTexto}/{capacidad}") { backStackEntry ->
            val sId = backStackEntry.arguments?.getString("servicioId") ?: ""
            val sNom = backStackEntry.arguments?.getString("servicioNombre") ?: ""
            val vId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val fecha = backStackEntry.arguments?.getString("fecha")?.replace("-", "/") ?: ""
            val hId = backStackEntry.arguments?.getString("horarioId") ?: ""
            val hTxt = backStackEntry.arguments?.getString("horaTexto") ?: ""
            val cap = backStackEntry.arguments?.getString("capacidad")?.toIntOrNull() ?: 1

            ConfirmarReservaScreen(
                clienteId = auth.currentUser?.uid ?: "",
                vehiculoId = vId,
                servicioId = sId,
                servicioNombre = sNom,
                fecha = fecha,
                horarioId = hId,
                horaTexto = hTxt,
                capacidadMaxima = cap,
                onReservaExitosa = {
                    navController.navigate(Screen.InicioCliente.route) {
                        popUpTo(Screen.InicioCliente.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
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
                onNavigateToUsuarios = { /* Navegar a Usuarios */ },
                onNavigateToServicios = { /* Navegar a Servicios */ },
                onNavigateToHorarios = { /* Navegar a Horarios */ },
                onNavigateToReservas = { /* Navegar a Reservas */ },
                onNavigateToReportes = { /* Navegar a Reportes */ }
            )
        }
    }
}
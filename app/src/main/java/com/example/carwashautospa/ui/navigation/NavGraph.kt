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
import com.example.carwashautospa.data.model.Horario
import com.example.carwashautospa.ui.administrador.DashboardAdminScreen
import com.example.carwashautospa.ui.auth.LoginScreen
import com.example.carwashautospa.ui.cliente.InicioClienteScreen
import com.example.carwashautospa.ui.cliente.reserva.ConfirmarReservaScreen
import com.example.carwashautospa.ui.cliente.reserva.SeleccionarFechaScreen
import com.example.carwashautospa.ui.cliente.reserva.SeleccionarHorarioScreen
import com.example.carwashautospa.ui.cliente.reserva.SeleccionarVehiculoScreen
import com.example.carwashautospa.ui.cliente.servicios.ServiciosScreen
import com.example.carwashautospa.ui.operario.DashboardOperarioScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SplashAuth : Screen("splash_auth")
    object InicioCliente : Screen("inicio_cliente")
    object DashboardOperario : Screen("dashboard_operario")
    object DashboardAdmin : Screen("dashboard_admin")

    object SeleccionarVehiculo : Screen("seleccionar_vehiculo")
    object SeleccionarServicio : Screen("seleccionar_servicio")
    object SeleccionarFecha : Screen("seleccionar_fecha")
    object SeleccionarHorario : Screen("seleccionar_horario")
    object ConfirmarReserva : Screen("confirmar_reserva")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val startDestination = if (auth.currentUser != null) Screen.SplashAuth.route else Screen.Login.route

    var reservaVehiculoId by remember { mutableStateOf("") }
    var reservaServicioId by remember { mutableStateOf("") }
    var reservaServicioNombre by remember { mutableStateOf("") }
    var reservaFecha by remember { mutableStateOf("") }
    var reservaHorario by remember { mutableStateOf<Horario?>(null) }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Screen.SplashAuth.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

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

        composable(Screen.InicioCliente.route) {
            InicioClienteScreen(
                onCerrarSesion = {
                    auth.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToReservar = {
                    reservaVehiculoId = ""
                    reservaServicioId = ""
                    reservaServicioNombre = ""
                    reservaFecha = ""
                    reservaHorario = null
                    navController.navigate(Screen.SeleccionarVehiculo.route)
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
                onNavigateToUsuarios = { },
                onNavigateToServicios = { },
                onNavigateToHorarios = { },
                onNavigateToReservas = { },
                onNavigateToReportes = { }
            )
        }

        composable(Screen.SeleccionarVehiculo.route) {
            SeleccionarVehiculoScreen(
                onVehiculoSeleccionado = { vehiculoId ->
                    reservaVehiculoId = vehiculoId
                    navController.navigate(Screen.SeleccionarServicio.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeleccionarServicio.route) {
            ServiciosScreen(
                onServicioSeleccionado = { servicioId, servicioNombre ->
                    reservaServicioId = servicioId
                    reservaServicioNombre = servicioNombre
                    navController.navigate(Screen.SeleccionarFecha.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeleccionarFecha.route) {
            SeleccionarFechaScreen(
                onFechaSeleccionada = { fecha ->
                    reservaFecha = fecha
                    navController.navigate(Screen.SeleccionarHorario.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeleccionarHorario.route) {
            SeleccionarHorarioScreen(
                fecha = reservaFecha,
                onHorarioSeleccionado = { horario ->
                    reservaHorario = horario
                    navController.navigate(Screen.ConfirmarReserva.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ConfirmarReserva.route) {
            val horario = reservaHorario
            if (horario != null) {
                ConfirmarReservaScreen(
                    clienteId = auth.currentUser?.uid ?: "",
                    vehiculoId = reservaVehiculoId,
                    servicioId = reservaServicioId,
                    servicioNombre = reservaServicioNombre,
                    fecha = reservaFecha,
                    horarioId = horario.id,
                    horaTexto = horario.horaInicio,
                    capacidadMaxima = horario.capacidad,
                    onReservaExitosa = {
                        navController.navigate(Screen.InicioCliente.route) {
                            popUpTo(Screen.InicioCliente.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
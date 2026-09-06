package com.example.carwashautospa.ui.cliente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
<<<<<<< HEAD
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
=======
>>>>>>> 7ad573d5d6d1130cdc15e656fa85c50763f280da
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

<<<<<<< HEAD
@OptIn(ExperimentalMaterial3Api::class)
=======
>>>>>>> 7ad573d5d6d1130cdc15e656fa85c50763f280da
@Composable
fun InicioClienteScreen(
    onCerrarSesion: () -> Unit,
    onNavigateToVehiculos: () -> Unit = {},
    onNavigateToReservar: () -> Unit = {},
    onNavigateToSeguimiento: () -> Unit = {},
    onNavigateToHistorial: () -> Unit = {},
    onNavigateToPerfil: () -> Unit = {}
) {
    var nombreUsuario by remember { mutableStateOf("Cliente") }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Obtener el nombre del usuario logueado
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    val nombre = document.getString("nombre")
                    if (!nombre.isNullOrEmpty()) {
                        nombreUsuario = nombre
                    }
                }
        }
    }

<<<<<<< HEAD
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CAR WASH-AUTO SPA", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Saludo personalizado
            Text(
                text = "Hola, $nombreUsuario 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿Qué deseas hacer?",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones / Tarjetas de opciones
            OpcionCard(
                texto = "🚗 Mis vehículos",
                onClick = onNavigateToVehiculos
            )

            Spacer(modifier = Modifier.height(16.dp))

            OpcionCard(
                texto = "📅 Reservar turno",
                onClick = onNavigateToReservar
            )

            Spacer(modifier = Modifier.height(16.dp))

            OpcionCard(
                texto = "🔄 Seguimiento",
                onClick = onNavigateToSeguimiento
            )

            Spacer(modifier = Modifier.height(16.dp))

            OpcionCard(
                texto = "📋 Historial",
                onClick = onNavigateToHistorial
            )

            Spacer(modifier = Modifier.height(16.dp))

            OpcionCard(
                texto = "👤 Mi perfil",
                onClick = onNavigateToPerfil
            )
        }
=======
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Encabezado
        Text(
            text = "CAR WASH-AUTO SPA",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Saludo personalizado
        Text(
            text = "Hola, $nombreUsuario 👋",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Qué deseas hacer?",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones / Tarjetas de opciones
        OpcionCard(
            texto = "🚗 Mis vehículos",
            onClick = onNavigateToVehiculos
        )

        Spacer(modifier = Modifier.height(16.dp))

        OpcionCard(
            texto = "📅 Reservar turno",
            onClick = onNavigateToReservar
        )

        Spacer(modifier = Modifier.height(16.dp))

        OpcionCard(
            texto = "🔄 Seguimiento",
            onClick = onNavigateToSeguimiento
        )

        Spacer(modifier = Modifier.height(16.dp))

        OpcionCard(
            texto = "📋 Historial",
            onClick = onNavigateToHistorial
        )

        Spacer(modifier = Modifier.height(16.dp))

        OpcionCard(
            texto = "👤 Mi perfil",
            onClick = onNavigateToPerfil
        )
>>>>>>> 7ad573d5d6d1130cdc15e656fa85c50763f280da
    }
}

@Composable
fun OpcionCard(
    texto: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = texto,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}
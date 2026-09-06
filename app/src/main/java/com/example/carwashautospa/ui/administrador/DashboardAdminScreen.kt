package com.example.carwashautospa.ui.administrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Reserva
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    onCerrarSesion: () -> Unit = {},
    onNavigateToUsuarios: () -> Unit,
    onNavigateToServicios: () -> Unit,
    onNavigateToHorarios: () -> Unit,
    onNavigateToReservas: () -> Unit,
    onNavigateToReportes: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var reservasHoy by remember { mutableStateOf(0) }
    var atencionesTotales by remember { mutableStateOf(0) }
    var enProceso by remember { mutableStateOf(0) }
    var listos by remember { mutableStateOf(0) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("reservas").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val reservas = snapshot.toObjects(Reserva::class.java)
                reservasHoy = reservas.size
                atencionesTotales = reservas.count { it.estado != "CANCELADA" }
                enProceso = reservas.count { it.estado == "EN PROCESO" }
                listos = reservas.count { it.estado == "LISTO PARA RECOGER" }
            }
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PANEL ADMINISTRADOR", fontWeight = FontWeight.Bold) },
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
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("RESUMEN DE OPERACIONES", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricaAdminCard("Reservas Hoy", reservasHoy.toString(), Modifier.weight(1f))
                    MetricaAdminCard("Atenciones", atencionesTotales.toString(), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricaAdminCard("En Proceso", enProceso.toString(), Modifier.weight(1f))
                    MetricaAdminCard("Listos", listos.toString(), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text("GESTIÓN MÓDULOS", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onNavigateToUsuarios, modifier = Modifier.fillMaxWidth()) { Text("GESTIONAR USUARIOS Y ROLES") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToServicios, modifier = Modifier.fillMaxWidth()) { Text("GESTIONAR SERVICIOS Y PRECIOS") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToHorarios, modifier = Modifier.fillMaxWidth()) { Text("CONFIGURAR HORARIOS Y CAPACIDAD") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToReservas, modifier = Modifier.fillMaxWidth()) { Text("TODAS LAS RESERVAS") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToReportes, modifier = Modifier.fillMaxWidth()) { Text("REPORTES Y ESTADÍSTICAS") }
            }
        }
    }
}

@Composable
fun MetricaAdminCard(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = titulo, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
package com.example.carwashautospa.ui.operario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Reserva
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtencionesScreen(
    onSeleccionarAtencion: (String) -> Unit,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var listaReservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Escuchar atenciones en tiempo real desde Firestore
    LaunchedEffect(Unit) {
        db.collection("reservas").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                listaReservas = snapshot.toObjects(Reserva::class.java)
            }
            cargando = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ATENCIONES DEL DÍA", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listaReservas.isEmpty()) {
                Text("No hay atenciones registradas.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaReservas) { reserva ->
                        AtencionCardItem(
                            reserva = reserva,
                            onClick = { onSeleccionarAtencion(reserva.id) },
                            onIniciarAtencion = {
                                db.collection("reservas")
                                    .document(reserva.id)
                                    .update("estado", "EN PROCESO")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AtencionCardItem(
    reserva: Reserva,
    onClick: () -> Unit,
    onIniciarAtencion: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var nombreVehiculo by remember { mutableStateOf("Cargando vehículo...") }
    var placaVehiculo by remember { mutableStateOf("") }
    var nombreServicio by remember { mutableStateOf("Cargando servicio...") }

    // Consultar datos del Vehículo y del Servicio
    LaunchedEffect(reserva) {
        if (reserva.vehiculoId.isNotEmpty()) {
            db.collection("vehiculos").document(reserva.vehiculoId).get()
                .addOnSuccessListener { doc ->
                    val marca = doc.getString("marca") ?: ""
                    val modelo = doc.getString("modelo") ?: ""
                    nombreVehiculo = "$marca $modelo"
                    placaVehiculo = doc.getString("placa") ?: ""
                }
        }

        if (reserva.servicioId.isNotEmpty()) {
            db.collection("servicios").document(reserva.servicioId).get()
                .addOnSuccessListener { doc ->
                    nombreServicio = doc.getString("nombre") ?: ""
                }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ID o Código de atención abreviado
            Text(
                text = "#${reserva.id.takeLast(4).uppercase()}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Datos del Vehículo
            Text(text = nombreVehiculo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (placaVehiculo.isNotEmpty()) {
                Text(text = placaVehiculo, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Datos del Servicio
            Text(text = nombreServicio, fontSize = 15.sp, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(12.dp))

            // Estado actual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Estado:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = reserva.estado,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = when (reserva.estado) {
                        "RECIBIDO" -> Color(0xFF0288D1)
                        "EN PROCESO" -> Color(0xFFE65100)
                        "LISTO PARA RECOGER" -> Color(0xFF2E7D32)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Botón "INICIAR ATENCIÓN" si el estado es RECIBIDO o RESERVADA
            if (reserva.estado == "RECIBIDO" || reserva.estado == "RESERVADA") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onIniciarAtencion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("INICIAR ATENCIÓN")
                }
            }
        }
    }
}
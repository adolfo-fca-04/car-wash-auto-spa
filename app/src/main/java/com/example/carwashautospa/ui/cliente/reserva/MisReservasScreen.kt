package com.example.carwashautospa.ui.cliente.reserva

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var listaReservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Escuchar reservas del usuario actual en tiempo real
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: ""
        db.collection("reservas")
            .whereEqualTo("clienteId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val reservas = snapshot.toObjects(Reserva::class.java)
                    listaReservas = reservas
                }
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MIS RESERVAS", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listaReservas.isEmpty()) {
                Text(
                    text = "No tienes reservas registradas.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listaReservas) { reserva ->
                        ReservaCard(
                            reserva = reserva,
                            onCancelar = {
                                db.collection("reservas")
                                    .document(reserva.id)
                                    .update("estado", "CANCELADA")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCard(
    reserva: Reserva,
    onCancelar: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var nombreVehiculo by remember { mutableStateOf("Cargando vehículo...") }
    var nombreServicio by remember { mutableStateOf("Cargando servicio...") }

    // Obtener datos complementarios del vehículo y servicio
    LaunchedEffect(reserva) {
        if (reserva.vehiculoId.isNotEmpty()) {
            db.collection("vehiculos").document(reserva.vehiculoId).get()
                .addOnSuccessListener { doc ->
                    val marca = doc.getString("marca") ?: ""
                    val modelo = doc.getString("modelo") ?: ""
                    nombreVehiculo = "$marca $modelo"
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = reserva.fecha,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = reserva.hora,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = nombreVehiculo, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(text = nombreServicio, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${reserva.estado}",
                    fontWeight = FontWeight.Bold,
                    color = when (reserva.estado) {
                        "RESERVADA" -> Color(0xFF0288D1)
                        "CANCELADA" -> MaterialTheme.colorScheme.error
                        else -> Color(0xFF2E7D32)
                    }
                )

                if (reserva.estado == "RESERVADA") {
                    OutlinedButton(onClick = onCancelar) {
                        Text("CANCELAR", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
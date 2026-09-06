package com.example.carwashautospa.ui.cliente.historial
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Reserva
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onSeleccionarHistorial: (String) -> Unit,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var listaHistorial by remember { mutableStateOf<List<Reserva>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: ""
        db.collection("reservas")
            .whereEqualTo("clienteId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val reservas = snapshot.toObjects(Reserva::class.java)
                // Filtrar solo las atenciones finalizadas o canceladas
                listaHistorial = reservas.filter { it.estado == "ENTREGADO" || it.estado == "CANCELADA" }
                cargando = false
            }
            .addOnFailureListener {
                cargando = false
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("HISTORIAL DE SERVICIOS", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listaHistorial.isEmpty()) {
                Text(
                    text = "No tienes servicios completados en tu historial.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaHistorial) { reserva ->
                        HistorialCardItem(
                            reserva = reserva,
                            onClick = { onSeleccionarHistorial(reserva.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialCardItem(
    reserva: Reserva,
    onClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var nombreVehiculo by remember { mutableStateOf("Cargando vehículo...") }
    var nombreServicio by remember { mutableStateOf("Cargando servicio...") }
    var precioServicio by remember { mutableStateOf("") }

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
                    precioServicio = "S/ ${doc.get("precio") ?: 0}"
                }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = nombreVehiculo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = reserva.fecha, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = nombreServicio, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                if (precioServicio.isNotEmpty()) {
                    Text(text = precioServicio, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Estado: ${reserva.estado}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
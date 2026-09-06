package com.example.carwashautospa.ui.cliente.historial

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Reserva
import com.example.carwashautospa.data.model.Servicio
import com.example.carwashautospa.data.model.Vehiculo
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleHistorialScreen(
    reservaId: String,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var reserva by remember { mutableStateOf<Reserva?>(null) }
    var vehiculo by remember { mutableStateOf<Vehiculo?>(null) }
    var servicio by remember { mutableStateOf<Servicio?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(reservaId) {
        db.collection("reservas").document(reservaId).get()
            .addOnSuccessListener { docRes ->
                reserva = docRes.toObject(Reserva::class.java)
                val res = reserva
                if (res != null) {
                    db.collection("vehiculos").document(res.vehiculoId).get()
                        .addOnSuccessListener { docVeh ->
                            vehiculo = docVeh.toObject(Vehiculo::class.java)
                            db.collection("servicios").document(res.servicioId).get()
                                .addOnSuccessListener { docSer ->
                                    servicio = docSer.toObject(Servicio::class.java)
                                    cargando = false
                                }
                        }
                } else {
                    cargando = false
                }
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("DETALLE DEL SERVICIO", fontWeight = FontWeight.Bold) }) }
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
                    .padding(24.dp)
            ) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("🚗 Vehículo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${vehiculo?.marca ?: ""} ${vehiculo?.modelo ?: ""}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("Placa: ${vehiculo?.placa ?: ""}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("🧽 Servicio Atendido", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(servicio?.nombre ?: "", fontSize = 18.sp, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("📅 Fecha y Hora", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${reserva?.fecha ?: ""} — ${reserva?.hora ?: ""}", fontSize = 16.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("💰 Total Pagado", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("S/ ${servicio?.precio ?: 0}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("📌 Estado Final", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(reserva?.estado ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
package com.example.carwashautospa.ui.operario

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
fun DetalleAtencionScreen(
    reservaId: String,
    onNavigateToCambiarEstado: (String) -> Unit,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var reserva by remember { mutableStateOf<Reserva?>(null) }
    var vehiculo by remember { mutableStateOf<Vehiculo?>(null) }
    var servicio by remember { mutableStateOf<Servicio?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(reservaId) {
        db.collection("reservas").document(reservaId).get().addOnSuccessListener { docRes ->
            reserva = docRes.toObject(Reserva::class.java)
            val res = reserva
            if (res != null) {
                db.collection("vehiculos").document(res.vehiculoId).get().addOnSuccessListener { docVeh ->
                    vehiculo = docVeh.toObject(Vehiculo::class.java)
                    db.collection("servicios").document(res.servicioId).get().addOnSuccessListener { docSer ->
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
        topBar = { TopAppBar(title = { Text("DETALLE DE ATENCIÓN", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "🚗 Vehículo:", fontWeight = FontWeight.Bold)
                    Text(text = "${vehiculo?.marca ?: ""} ${vehiculo?.modelo ?: ""} - Placa: ${vehiculo?.placa ?: ""}", fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "🧽 Servicio:", fontWeight = FontWeight.Bold)
                    Text(text = servicio?.nombre ?: "", fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "📌 Estado actual:", fontWeight = FontWeight.Bold)
                    Text(text = reserva?.estado ?: "", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onNavigateToCambiarEstado(reservaId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("CAMBIAR ESTADO")
                }
            }
        }
    }
}
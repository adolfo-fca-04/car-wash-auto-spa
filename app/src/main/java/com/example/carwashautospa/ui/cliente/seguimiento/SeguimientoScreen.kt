package com.example.carwashautospa.ui.cliente.seguimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.carwashautospa.data.model.Vehiculo
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoScreen(
    reservaId: String,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var reserva by remember { mutableStateOf<Reserva?>(null) }
    var vehiculo by remember { mutableStateOf<Vehiculo?>(null) }
    var cargando by remember { mutableStateOf(true) }

    // Escuchar la reserva en TIEMPO REAL con addSnapshotListener
    LaunchedEffect(reservaId) {
        db.collection("reservas").document(reservaId)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
                    val resData = snapshot.toObject(Reserva::class.java)
                    reserva = resData

                    // Cargar los datos del vehículo asociado
                    resData?.vehiculoId?.let { vId ->
                        db.collection("vehiculos").document(vId).get()
                            .addOnSuccessListener { docVeh ->
                                vehiculo = docVeh.toObject(Vehiculo::class.java)
                                cargando = false
                            }
                    }
                } else {
                    cargando = false
                }
            }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("SEGUIMIENTO", fontWeight = FontWeight.Bold) }) }
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Información del Vehículo
                Text(
                    text = "${vehiculo?.marca?.uppercase() ?: ""} ${vehiculo?.modelo?.uppercase() ?: ""}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vehiculo?.placa?.uppercase() ?: "",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "ESTADO DEL SERVICIO",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Indicador visual de los estados
                val estadoActual = reserva?.estado ?: "RECIBIDO"

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PasoEstadoItem(nombre = "RECIBIDO", estadoActual = estadoActual)
                        PasoEstadoItem(nombre = "EN ESPERA", estadoActual = estadoActual)
                        PasoEstadoItem(nombre = "EN PROCESO", estadoActual = estadoActual)
                        PasoEstadoItem(nombre = "LISTO PARA RECOGER", estadoActual = estadoActual)
                        PasoEstadoItem(nombre = "ENTREGADO", estadoActual = estadoActual)
                    }
                }
            }
        }
    }
}

@Composable
fun PasoEstadoItem(
    nombre: String,
    estadoActual: String
) {
    val ordenEstados = listOf("RECIBIDO", "EN ESPERA", "EN PROCESO", "LISTO PARA RECOGER", "ENTREGADO")
    val indiceActual = ordenEstados.indexOf(estadoActual)
    val indiceItem = ordenEstados.indexOf(nombre)

    val esCompletado = indiceItem < indiceActual
    val esActual = indiceItem == indiceActual

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icono de estado (Completado / Actual / Pendiente)
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = when {
                        esCompletado -> Color(0xFF2E7D32) // Verde
                        esActual -> MaterialTheme.colorScheme.primary // Azul/Primario
                        else -> Color.LightGray
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    esCompletado -> "✓"
                    esActual -> "●"
                    else -> "○"
                },
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nombre del estado
        Text(
            text = nombre,
            fontSize = 16.sp,
            fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal,
            color = if (esActual) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
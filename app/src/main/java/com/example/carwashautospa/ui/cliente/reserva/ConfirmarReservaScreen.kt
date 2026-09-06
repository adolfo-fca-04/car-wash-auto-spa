package com.example.carwashautospa.ui.cliente.reserva

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carwashautospa.util.ValidationUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ConfirmarReservaScreen(
    clienteId: String,
    vehiculoId: String,
    servicioId: String,
    servicioNombre: String,
    fecha: String,
    horarioId: String,
    horaTexto: String,
    capacidadMaxima: Int,
    onReservaExitosa: () -> Unit,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Servicio: $servicioNombre")
        Text("Fecha: $fecha   Hora: $horaTexto")

        Spacer(modifier = Modifier.height(24.dp))

        if (cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    cargando = true
                    procesarReservaConTransaccion(
                        db = db,
                        clienteId = clienteId,
                        vehiculoId = vehiculoId,
                        servicioId = servicioId,
                        servicioNombre = servicioNombre,
                        fecha = fecha,
                        horarioId = horarioId,
                        horaTexto = horaTexto,
                        capacidadMaxima = capacidadMaxima,
                        onSuccess = {
                            cargando = false
                            onReservaExitosa()
                        },
                        onError = { error ->
                            cargando = false
                            mensajeError = error
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CONFIRMAR RESERVA")
            }
        }

        if (mensajeError != null) {
            AlertDialog(
                onDismissRequest = { mensajeError = null },
                title = { Text("Aviso de Reserva", fontWeight = FontWeight.Bold) },
                text = { Text(mensajeError!!) },
                confirmButton = {
                    Button(onClick = { mensajeError = null }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}

fun procesarReservaConTransaccion(
    db: FirebaseFirestore,
    clienteId: String,
    vehiculoId: String,
    servicioId: String,
    servicioNombre: String,
    fecha: String,
    horarioId: String,
    horaTexto: String,
    capacidadMaxima: Int,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (vehiculoId.isBlank()) {
        onError("Debes seleccionar un vehículo válido.")
        return
    }

    if (!ValidationUtils.esFechaFuturaOActual(fecha)) {
        onError("No puedes seleccionar una fecha anterior a la actual.")
        return
    }

    val horarioRef = db.collection("horarios").document(horarioId)
    val nuevaReservaRef = db.collection("reservas").document()

    db.runTransaction { transaction ->
        val horarioSnapshot = transaction.get(horarioRef)
        val cuposOcupados = horarioSnapshot.getLong("cuposOcupados") ?: 0L

        if (cuposOcupados >= capacidadMaxima) {
            throw Exception("❌ No puedes reservar este horario.\n\nEl horario seleccionado ya alcanzó su capacidad máxima.")
        }

        val datosReserva = mapOf(
            "id" to nuevaReservaRef.id,
            "clienteId" to clienteId,
            "vehiculoId" to vehiculoId,
            "servicioId" to servicioId,
            "servicioNombre" to servicioNombre,
            "horarioId" to horarioId,
            "fecha" to fecha,
            "hora" to horaTexto,
            "estado" to "RESERVADA",
            "timestamp" to Timestamp.now()
        )
        transaction.set(nuevaReservaRef, datosReserva)
        transaction.update(horarioRef, "cuposOcupados", cuposOcupados + 1)

        null
    }.addOnSuccessListener {
        onSuccess()
    }.addOnFailureListener { exception ->
        onError(exception.message ?: "Error al procesar la reserva.")
    }
}
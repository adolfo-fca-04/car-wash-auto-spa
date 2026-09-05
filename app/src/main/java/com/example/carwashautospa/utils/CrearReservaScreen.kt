package com.example.carwashautospa.ui.cliente.reserva

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.example.carwashautospa.util.ValidationUtils
import com.google.firebase.firestore.FirebaseFirestore

// 1. FUNCIÓN DE PROCESAMIENTO CORREGIDA
fun procesarReserva(
    db: FirebaseFirestore,
    usuarioId: String,
    vehiculoId: String,
    servicioId: String,
    fecha: String,
    horarioId: String,
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

    // Validar reservas existentes en la misma fecha y horario
    db.collection("reservas")
        .whereEqualTo("fecha", fecha)
        .whereEqualTo("horarioId", horarioId)
        .get()
        .addOnSuccessListener { snapshot ->
            val reservasEnHorario = snapshot.documents

            // Validar Reserva Duplicada para el mismo vehículo
            val yaTieneReserva = reservasEnHorario.any {
                it.getString("vehiculoId") == vehiculoId && it.getString("estado") != "CANCELADA"
            }

            if (yaTieneReserva) {
                onError("Este vehículo ya tiene una reserva activa para este horario.")
                return@addOnSuccessListener
            }

            // Validar Horario Lleno (Capacidad Máxima)
            val reservasActivas = reservasEnHorario.count { it.getString("estado") != "CANCELADA" }
            if (reservasActivas >= capacidadMaxima) {
                onError("❌ No puedes reservar este horario.\n\nEl horario seleccionado ya alcanzó su capacidad máxima.")
                return@addOnSuccessListener
            }

            // Guardar la nueva reserva
            val docRef = db.collection("reservas").document()
            val nuevaReserva = mapOf(
                "id" to docRef.id,
                "usuarioId" to usuarioId,
                "vehiculoId" to vehiculoId,
                "servicioId" to servicioId,
                "fecha" to fecha,
                "horarioId" to horarioId,
                "estado" to "RESERVADA",
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            docRef.set(nuevaReserva).addOnSuccessListener {
                onSuccess()
            }
        }
        .addOnFailureListener { // CORREGIDO: addOnFailureListener en lugar de addOnFailureError
            onError("Error al verificar disponibilidad del horario.")
        }
}

// 2. EJEMPLO DE CÓMO USAR EL DIÁLOGO EN TU PANTALLA (@Composable)
@Composable
fun DialogoErrorReserva(
    mensajeError: String?,
    onDismiss: () -> Unit
) {
    if (mensajeError != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Aviso de Reserva", fontWeight = FontWeight.Bold) },
            text = { Text(mensajeError) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Entendido")
                }
            }
        )
    }
}
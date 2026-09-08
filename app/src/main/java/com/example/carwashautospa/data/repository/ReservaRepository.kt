package com.example.carwashautospa.data.repository

import com.example.carwashautospa.data.model.EstadoAtencion
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Repositorio con la regla de negocio para actualizar el estado del vehículo en Firestore.
 */
class ReservaRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = db.collection("reservas")

    /**
     * Actualiza el estado de la reserva siguiendo la máquina de estados de EstadoAtencion.
     */
    fun actualizarEstado(
        reservaId: String,
        nuevoEstado: EstadoAtencion,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        collection.document(reservaId)
            .update("estado", nuevoEstado.codigo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}

package com.example.carwashautospa.data.model

/**
 * Modelo de Reserva integrado con el ciclo de lavado de EstadoAtencion.
 * Mantiene compatibilidad con horarioId de la rama main.
 */
data class Reserva(
    val id: String = "",
    val clienteId: String = "",
    val vehiculoId: String = "",
    val servicioId: String = "",
    val servicioNombre: String = "",
    val horarioId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val estado: String = EstadoAtencion.RESERVADA.codigo
) {
    fun obtenerEstadoAtencion(): EstadoAtencion = EstadoAtencion.desdeCodigo(estado)
}

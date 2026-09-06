package com.example.carwashautospa.data.model

data class Reserva(
    val id: String = "",
    val clienteId: String = "",
    val vehiculoId: String = "",
    val servicioId: String = "",
    val servicioNombre: String = "",
    val horarioId: String = "",
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "PENDIENTE"
)
package com.example.carwashautospa.data.model

data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val duracionMinutos: Int = 30,
    val activo: Boolean = true
)
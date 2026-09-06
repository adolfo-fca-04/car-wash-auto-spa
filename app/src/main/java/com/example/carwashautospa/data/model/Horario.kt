package com.example.carwashautospa.data.model

data class Horario(
    val id: String = "",
    val fecha: String = "",
    val horaInicio: String = "",
    val horaFin: String = "",
    val capacidad: Int = 0,
    val cuposOcupados: Int = 0
)
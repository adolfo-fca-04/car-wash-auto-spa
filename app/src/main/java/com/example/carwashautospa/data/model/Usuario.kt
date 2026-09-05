package com.example.carwashautospa.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val rol: String = "CLIENTE" // CLIENTE, OPERARIO, ADMINISTRADOR
)
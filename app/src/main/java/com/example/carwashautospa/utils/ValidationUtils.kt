package com.example.carwashautospa.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ValidationUtils {

    fun esCorreoValido(correo: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    fun esContrasenaValida(contrasena: String): Boolean {
        return contrasena.length >= 6
    }

    fun esPlacaValida(placa: String): Boolean {
        return placa.trim().isNotBlank() && placa.trim().length >= 6
    }

    fun esFechaFuturaOActual(fechaStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaIngresada = sdf.parse(fechaStr) ?: return false
            val fechaHoy = sdf.parse(sdf.format(Date())) ?: return false
            !fechaIngresada.before(fechaHoy)
        } catch (e: Exception) {
            false
        }
    }
}
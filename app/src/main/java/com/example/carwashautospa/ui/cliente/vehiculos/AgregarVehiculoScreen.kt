package com.example.carwashautospa.ui.cliente.vehiculos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Vehiculo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AgregarVehiculoScreen(
    onVehiculoAgregado: () -> Unit
) {
    var placa by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "REGISTRAR VEHÍCULO",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = placa,
            onValueChange = { placa = it.uppercase() },
            label = { Text("Placa (ej: ABC-123)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca (ej: Toyota)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = modelo,
            onValueChange = { modelo = it },
            label = { Text("Modelo (ej: Corolla)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color (ej: Blanco)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (placa.isNotEmpty() && marca.isNotEmpty() && modelo.isNotEmpty() && color.isNotEmpty()) {
                    cargando = true
                    mensajeError = ""
                    val uid = auth.currentUser?.uid ?: ""
                    val docRef = db.collection("vehiculos").document()

                    val nuevoVehiculo = Vehiculo(
                        id = docRef.id,
                        propietarioId = uid,
                        placa = placa,
                        marca = marca,
                        modelo = modelo,
                        color = color
                    )

                    docRef.set(nuevoVehiculo)
                        .addOnSuccessListener {
                            cargando = false
                            onVehiculoAgregado()
                        }
                        .addOnFailureListener { e ->
                            cargando = false
                            mensajeError = "Error al registrar: ${e.localizedMessage}"
                        }
                } else {
                    mensajeError = "Completa todos los campos"
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (cargando) "GUARDANDO..." else "GUARDAR VEHÍCULO")
        }
    }
}
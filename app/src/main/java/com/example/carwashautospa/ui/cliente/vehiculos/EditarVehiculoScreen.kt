package com.example.carwashautospa.ui.cliente.vehiculos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditarVehiculoScreen(
    vehiculoId: String,
    onVehiculoEditado: () -> Unit
) {
    var placa by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    // Cargar datos actuales del vehículo
    LaunchedEffect(vehiculoId) {
        db.collection("vehiculos").document(vehiculoId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    placa = doc.getString("placa") ?: ""
                    marca = doc.getString("marca") ?: ""
                    modelo = doc.getString("modelo") ?: ""
                    color = doc.getString("color") ?: ""
                }
                cargando = false
            }
    }

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "EDITAR VEHÍCULO",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = placa,
                onValueChange = { placa = it.uppercase() },
                label = { Text("Placa") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = { Text("Modelo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
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
                        val updates = mapOf(
                            "placa" to placa,
                            "marca" to marca,
                            "modelo" to modelo,
                            "color" to color
                        )

                        db.collection("vehiculos").document(vehiculoId)
                            .update(updates)
                            .addOnSuccessListener {
                                onVehiculoEditado()
                            }
                            .addOnFailureListener { e ->
                                mensajeError = "Error al actualizar: ${e.localizedMessage}"
                            }
                    } else {
                        mensajeError = "Completa todos los campos"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ACTUALIZAR VEHÍCULO")
            }
        }
    }
}

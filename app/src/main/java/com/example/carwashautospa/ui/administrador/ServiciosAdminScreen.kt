package com.example.carwashautospa.ui.administrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Servicio
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosAdminScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var servicios by remember { mutableStateOf<List<Servicio>>(emptyList()) }

    var servicioEditar by remember { mutableStateOf<Servicio?>(null) }
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    // Escuchar servicios en tiempo real
    LaunchedEffect(Unit) {
        db.collection("servicios").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                servicios = snapshot.toObjects(Servicio::class.java)
            }
        }
    }

    fun limpiarCampos() {
        servicioEditar = null
        nombre = ""
        precio = ""
        duracion = ""
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("GESTIÓN DE SERVICIOS", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Formulario CREAR / EDITAR
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (servicioEditar == null) "CREAR SERVICIO" else "EDITAR SERVICIO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Servicio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = precio,
                            onValueChange = { precio = it },
                            label = { Text("Precio (S/)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = duracion,
                            onValueChange = { duracion = it },
                            label = { Text("Duración (min)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (servicioEditar != null) {
                            OutlinedButton(
                                onClick = { limpiarCampos() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("CANCELAR")
                            }
                        }

                        Button(
                            onClick = {
                                if (nombre.isNotEmpty() && precio.isNotEmpty()) {
                                    guardando = true
                                    val idDoc = servicioEditar?.id ?: db.collection("servicios").document().id
                                    val nuevoServicio = Servicio(
                                        id = idDoc,
                                        nombre = nombre,
                                        descripcion = "",
                                        precio = precio.toDoubleOrNull() ?: 0.0,
                                        duracionMinutos = duracion.toIntOrNull() ?: 30,
                                        activo = servicioEditar?.activo ?: true
                                    )

                                    db.collection("servicios").document(idDoc).set(nuevoServicio)
                                        .addOnSuccessListener {
                                            guardando = false
                                            limpiarCampos()
                                        }
                                }
                            },
                            enabled = !guardando,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (servicioEditar == null) "GUARDAR" else "ACTUALIZAR")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("CONSULTAR SERVICIOS", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Lista CONSULTAR
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(servicios) { ser ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ser.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (ser.activo) "ACTIVO" else "DESACTIVADO",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (ser.activo) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "S/ ${ser.precio.toInt()} • ${ser.duracionMinutos} min",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        servicioEditar = ser
                                        nombre = ser.nombre
                                        precio = ser.precio.toString()
                                        duracion = ser.duracionMinutos.toString()
                                    }
                                ) {
                                    Text("EDITAR")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (ser.activo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    ),
                                    onClick = {
                                        db.collection("servicios").document(ser.id)
                                            .update("activo", !ser.activo)
                                    }
                                ) {
                                    Text(if (ser.activo) "DESACTIVAR" else "ACTIVAR")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
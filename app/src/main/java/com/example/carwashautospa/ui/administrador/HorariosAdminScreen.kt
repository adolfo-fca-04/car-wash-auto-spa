package com.example.carwashautospa.ui.administrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Horario
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorariosAdminScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var horarios by remember { mutableStateOf<List<Horario>>(emptyList()) }

    var fecha by remember { mutableStateOf("05/09/2026") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("5") }
    var guardando by remember { mutableStateOf(false) }

    // Cargar horarios desde Firestore
    LaunchedEffect(Unit) {
        db.collection("horarios").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                horarios = snapshot.toObjects(Horario::class.java)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("CONFIGURACIÓN DE HORARIOS", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Formulario de Registro
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CONFIGURAR BLOQUE HORARIO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("Fecha (dd/mm/yyyy)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { horaInicio = it },
                            label = { Text("Inicio (ej: 08:00)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { horaFin = it },
                            label = { Text("Fin (ej: 09:00)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = capacidad,
                        onValueChange = { capacidad = it },
                        label = { Text("Capacidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (fecha.isNotEmpty() && horaInicio.isNotEmpty() && horaFin.isNotEmpty()) {
                                guardando = true
                                val docRef = db.collection("horarios").document()
                                val nuevoHorario = mapOf(
                                    "id" to docRef.id,
                                    "fecha" to fecha,
                                    "horaInicio" to horaInicio,
                                    "horaFin" to horaFin,
                                    "capacidad" to (capacidad.toIntOrNull() ?: 5)
                                )

                                docRef.set(nuevoHorario).addOnSuccessListener {
                                    guardando = false
                                    horaInicio = ""
                                    horaFin = ""
                                }
                            }
                        },
                        enabled = !guardando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("GUARDAR HORARIO")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Agrupación y Lista por Fecha
            val horariosPorFecha = horarios.groupBy { it.fecha.ifEmpty { "05/09/2026" } }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                horariosPorFecha.forEach { (fechaGrupo, listaBloques) ->
                    item {
                        Column {
                            Text(
                                text = fechaGrupo,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            listaBloques.forEach { hor ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${hor.horaInicio} - ${hor.horaFin}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "Capacidad: ${hor.capacidad}",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.carwashautospa.ui.cliente.reserva

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
import com.example.carwashautospa.data.model.Horario
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarHorarioScreen(
    fecha: String,
    onHorarioSeleccionado: (String) -> Unit,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var listaHorarios by remember { mutableStateOf<List<Horario>>(emptyList()) }
    var reservasPorHora by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(fecha) {
        // 1. Obtener horarios configurados
        db.collection("horarios").get().addOnSuccessListener { horariosSnapshot ->
            val horarios = horariosSnapshot.toObjects(Horario::class.java)
            listaHorarios = horarios

            // 2. Obtener reservas hechas para la fecha elegida
            db.collection("reservas").whereEqualTo("fecha", fecha).get()
                .addOnSuccessListener { reservasSnapshot ->
                    val conteo = mutableMapOf<String, Int>()
                    for (doc in reservasSnapshot.documents) {
                        val hora = doc.getString("hora") ?: ""
                        if (hora.isNotEmpty()) {
                            conteo[hora] = conteo.getOrDefault(hora, 0) + 1
                        }
                    }
                    reservasPorHora = conteo
                    cargando = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PASO 4: HORARIOS DISPONIBLES", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listaHorarios.isEmpty()) {
                Text(
                    text = "No hay horarios disponibles para esta fecha.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaHorarios) { horario ->
                        val ocupados = reservasPorHora.getOrDefault(horario.horaInicio, 0)
                        val disponibles = horario.capacidad - ocupados

                        HorarioCard(
                            horario = horario,
                            disponibles = disponibles,
                            onSeleccionar = { onHorarioSeleccionado(horario.horaInicio) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HorarioCard(
    horario: Horario,
    disponibles: Int,
    onSeleccionar: () -> Unit
) {
    val estaDisponible = disponibles > 0

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${horario.horaInicio} - ${horario.horaFin}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (estaDisponible) {
                    Text(
                        text = "🟢 $disponibles disponibles",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "🔴 COMPLETO",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onSeleccionar,
                enabled = estaDisponible
            ) {
                Text("SELECCIONAR")
            }
        }
    }
}
package com.example.carwashautospa.ui.operario

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarEstadoScreen(
    reservaId: String,
    onEstadoCambiado: () -> Unit,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var estadoActual by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }

    // Obtener el estado actual de la reserva
    LaunchedEffect(reservaId) {
        db.collection("reservas").document(reservaId).get()
            .addOnSuccessListener { doc ->
                estadoActual = doc.getString("estado") ?: "RECIBIDO"
                cargando = false
            }
    }

    // Define la transición estricta de estados
    val siguienteEstado = when (estadoActual) {
        "RESERVADA", "RECIBIDO" -> "EN ESPERA"
        "EN ESPERA" -> "EN PROCESO"
        "EN PROCESO" -> "LISTO PARA RECOGER"
        "LISTO PARA RECOGER" -> "ENTREGADO"
        else -> null // Si ya está ENTREGADO o CANCELADA, no hay siguiente estado
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("CAMBIAR ESTADO", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Estado actual:", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = estadoActual,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (siguienteEstado != null) {
                        Text("Siguiente estado permitido:", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "➔ $siguienteEstado",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "El servicio ha finalizado. No hay cambios posteriores permitidos.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (siguienteEstado != null) {
                    Button(
                        onClick = {
                            guardando = true
                            db.collection("reservas").document(reservaId)
                                .update("estado", siguienteEstado)
                                .addOnSuccessListener {
                                    guardando = false
                                    onEstadoCambiado()
                                }
                        },
                        enabled = !guardando,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(if (guardando) "ACTUALIZANDO..." else "AVANZAR A $siguienteEstado")
                    }
                }
            }
        }
    }
}
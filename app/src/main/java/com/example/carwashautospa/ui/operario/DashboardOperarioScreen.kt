package com.example.carwashautospa.ui.operario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Reserva
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardOperarioScreen(
    onCerrarSesion: () -> Unit = {},
    onNavigateToAtenciones: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    var total by remember { mutableStateOf(0) }
    var recibidos by remember { mutableStateOf(0) }
    var enEspera by remember { mutableStateOf(0) }
    var enProceso by remember { mutableStateOf(0) }
    var listos by remember { mutableStateOf(0) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("reservas").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val reservas = snapshot.toObjects(Reserva::class.java)
                total = reservas.size
                recibidos = reservas.count { it.estado == "RECIBIDO" || it.estado == "RESERVADA" }
                enEspera = reservas.count { it.estado == "EN ESPERA" }
                enProceso = reservas.count { it.estado == "EN PROCESO" }
                listos = reservas.count { it.estado == "LISTO PARA RECOGER" }
            }
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PANEL OPERARIO", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        }
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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("ATENCIONES DE HOY", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))

                MetricaCard(titulo = "Total Atenciones", valor = total.toString(), color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                MetricaCard(titulo = "Recibidos", valor = recibidos.toString(), color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(12.dp))
                MetricaCard(titulo = "En Espera", valor = enEspera.toString(), color = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.height(12.dp))
                MetricaCard(titulo = "En Proceso", valor = enProceso.toString(), color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(12.dp))
                MetricaCard(titulo = "Listos para Recoger", valor = listos.toString(), color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onNavigateToAtenciones,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("VER LISTA DE ATENCIONES")
                }
            }
        }
    }
}

@Composable
fun MetricaCard(titulo: String, valor: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = valor, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
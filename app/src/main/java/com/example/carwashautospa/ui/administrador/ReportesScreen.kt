package com.example.carwashautospa.ui.administrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun ReportesScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var cargando by remember { mutableStateOf(true) }

    // Métricas Servicios
    var lavadoBasico by remember { mutableStateOf(0) }
    var lavadoCompleto by remember { mutableStateOf(0) }
    var lavadoPremium by remember { mutableStateOf(0) }
    var totalServicios by remember { mutableStateOf(0) }

    // Métricas Reservas
    var reservasRealizadas by remember { mutableStateOf(0) }
    var reservasAtendidas by remember { mutableStateOf(0) }
    var reservasCanceladas by remember { mutableStateOf(0) }

    // Métricas Atenciones
    var entregados by remember { mutableStateOf(0) }
    var enProceso by remember { mutableStateOf(0) }
    var pendientes by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        db.collection("reservas").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val reservas = snapshot.toObjects(Reserva::class.java)

                // Conteo Servicios
                lavadoBasico = reservas.count { it.servicioNombre.contains("básico", ignoreCase = true) }
                lavadoCompleto = reservas.count { it.servicioNombre.contains("completo", ignoreCase = true) }
                lavadoPremium = reservas.count { it.servicioNombre.contains("premium", ignoreCase = true) }
                totalServicios = reservas.size

                // Conteo Reservas
                reservasRealizadas = reservas.size
                reservasAtendidas = reservas.count { it.estado == "ENTREGADO" || it.estado == "LISTO PARA RECOGER" || it.estado == "EN PROCESO" }
                reservasCanceladas = reservas.count { it.estado == "CANCELADA" }

                // Conteo Atenciones
                entregados = reservas.count { it.estado == "ENTREGADO" }
                enProceso = reservas.count { it.estado == "EN PROCESO" }
                pendientes = reservas.count { it.estado == "RESERVADA" || it.estado == "PENDIENTE" }
            }
            cargando = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("REPORTES", fontWeight = FontWeight.Bold) }) }
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECCIÓN 1: SERVICIOS DEL MES
                CardReporteSection(titulo = "SERVICIOS DEL MES") {
                    FilaReporte("Lavado básico", lavadoBasico.toString())
                    FilaReporte("Lavado completo", lavadoCompleto.toString())
                    FilaReporte("Lavado premium", lavadoPremium.toString())
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    FilaReporte("TOTAL", totalServicios.toString(), esTotal = true)
                }

                // SECCIÓN 2: RESERVAS
                CardReporteSection(titulo = "RESERVAS") {
                    FilaReporte("Reservas realizadas", reservasRealizadas.toString())
                    FilaReporte("Reservas atendidas", reservasAtendidas.toString())
                    FilaReporte("Reservas canceladas", reservasCanceladas.toString())
                }

                // SECCIÓN 3: ATENCIONES
                CardReporteSection(titulo = "ATENCIONES") {
                    FilaReporte("Entregados", entregados.toString())
                    FilaReporte("En proceso", enProceso.toString())
                    FilaReporte("Pendientes", pendientes.toString())
                }
            }
        }
    }
}

@Composable
fun CardReporteSection(titulo: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun FilaReporte(concepto: String, cantidad: String, esTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = concepto,
            fontSize = if (esTotal) 16.sp else 14.sp,
            fontWeight = if (esTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = cantidad,
            fontSize = if (esTotal) 16.sp else 14.sp,
            fontWeight = if (esTotal) FontWeight.Bold else FontWeight.Medium,
            color = if (esTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
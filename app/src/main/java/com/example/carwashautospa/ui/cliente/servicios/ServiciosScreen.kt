package com.example.carwashautospa.ui.cliente.servicios

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
import com.example.carwashautospa.data.model.Servicio
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    onServicioSeleccionado: (String, String) -> Unit, // Pasa id y nombre del servicio
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var listaServicios by remember { mutableStateOf<List<Servicio>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Cargar la colección de servicios desde Firestore en tiempo real
    LaunchedEffect(Unit) {
        db.collection("servicios")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val servicios = snapshot.toObjects(Servicio::class.java)
                    listaServicios = servicios
                }
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SERVICIOS", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (listaServicios.isEmpty()) {
                Text(
                    text = "No hay servicios disponibles en este momento.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listaServicios) { servicio ->
                        ServicioCard(
                            servicio = servicio,
                            onSeleccionar = { onServicioSeleccionado(servicio.id, servicio.nombre) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServicioCard(
    servicio: Servicio,
    onSeleccionar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = servicio.nombre,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (servicio.descripcion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = servicio.descripcion,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "S/ ${servicio.precio}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "${servicio.duracionMinutos} min",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSeleccionar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SELECCIONAR")
            }
        }
    }
}
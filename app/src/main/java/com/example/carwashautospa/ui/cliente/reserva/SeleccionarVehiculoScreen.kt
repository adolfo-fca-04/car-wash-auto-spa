package com.example.carwashautospa.ui.cliente.reserva

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
import com.example.carwashautospa.data.model.Vehiculo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarVehiculoScreen(
    onVehiculoSeleccionado: (String) -> Unit,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var listaVehiculos by remember { mutableStateOf<List<Vehiculo>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: ""
        db.collection("vehiculos")
            .whereEqualTo("propietarioId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    listaVehiculos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Vehiculo::class.java)?.copy(id = doc.id)
                    }
                }
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PASO 1: SELECCIONAR VEHÍCULO", fontWeight = FontWeight.Bold) })
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
            } else if (listaVehiculos.isEmpty()) {
                Text(
                    text = "No tienes vehículos registrados. Agrega uno primero.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaVehiculos) { vehiculo ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${vehiculo.marca} ${vehiculo.modelo}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("Placa: ${vehiculo.placa}", fontSize = 14.sp)
                                }
                                Button(onClick = { onVehiculoSeleccionado(vehiculo.id) }) {
                                    Text("SELECCIONAR")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
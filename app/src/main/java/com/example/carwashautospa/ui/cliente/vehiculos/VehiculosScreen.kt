package com.example.carwashautospa.ui.cliente.vehiculos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
fun VehiculosScreen(
    onNavigateToAgregar: () -> Unit,
    onNavigateToEditar: (String) -> Unit,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var listaVehiculos by remember { mutableStateOf<List<Vehiculo>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Cargar vehículos del usuario actual
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: ""
        db.collection("vehiculos")
            .whereEqualTo("propietarioId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val vehiculos = snapshot.toObjects(Vehiculo::class.java)
                    listaVehiculos = vehiculos
                }
                cargando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MIS VEHÍCULOS") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAgregar) {
                Icon(Icons.Default.Add, contentDescription = "Agregar vehículo")
            }
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
            } else if (listaVehiculos.isEmpty()) {
                Text(
                    text = "No tienes vehículos registrados",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaVehiculos) { vehiculo ->
                        VehiculoCard(
                            vehiculo = vehiculo,
                            onEditar = { onNavigateToEditar(vehiculo.id) },
                            onEliminar = {
                                db.collection("vehiculos").document(vehiculo.id).delete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VehiculoCard(
    vehiculo: Vehiculo,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${vehiculo.marca} ${vehiculo.modelo}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Placa: ${vehiculo.placa}", fontSize = 14.sp)
                Text(text = "Color: ${vehiculo.color}", fontSize = 14.sp)
            }
            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
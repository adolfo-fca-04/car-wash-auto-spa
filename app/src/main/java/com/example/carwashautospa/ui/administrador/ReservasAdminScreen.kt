package com.example.carwashautospa.ui.administrador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.carwashautospa.data.model.Reserva
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasAdminScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var reservas by remember { mutableStateOf<List<Reserva>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("reservas").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                reservas = snapshot.toObjects(Reserva::class.java)
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("RESERVAS REGISTRADAS", fontWeight = FontWeight.Bold) }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reservas) { res ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fecha: ${res.fecha} | Hora: ${res.hora}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Estado: ${res.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
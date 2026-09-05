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
fun AtencionesAdminScreen(onBack: () -> Unit) {

    val db = FirebaseFirestore.getInstance()

    var atenciones by remember {
        mutableStateOf<List<Reserva>>(emptyList())
    }

    LaunchedEffect(Unit) {
        db.collection("reservas")
            .addSnapshotListener { snapshot, error ->

                if (error == null && snapshot != null) {

                    atenciones = snapshot
                        .toObjects(Reserva::class.java)
                        .filter { it.estado != "RESERVADA" }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ATENCIONES EN TALLER",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(atenciones) { at ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            "ID: #${at.id.takeLast(4).uppercase()}",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Estado Taller: ${at.estado}",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


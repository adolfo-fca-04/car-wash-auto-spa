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
import com.example.carwashautospa.data.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var listaUsuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("usuarios").addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                listaUsuarios = snapshot.toObjects(Usuario::class.java)
            }
            cargando = false
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("GESTIÓN DE USUARIOS", fontWeight = FontWeight.Bold) }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaUsuarios) { usuario ->
                        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(text = usuario.correo, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Rol: ${usuario.rol}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Button(onClick = {
                                        val nuevoRol = if (usuario.rol == "CLIENTE") "OPERARIO" else "CLIENTE"
                                        db.collection("usuarios").document(usuario.uid).update("rol", nuevoRol)
                                    }) { Text("CAMBIAR ROL") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.carwashautospa.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegistroScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: (String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "REGISTRO DE USUARIO",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Teléfono
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Confirmar contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón CREAR CUENTA
        Button(
            onClick = {
                when {
                    nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                        mensajeError = "Por favor completa todos los campos"
                    }
                    password != confirmPassword -> {
                        mensajeError = "Las contraseñas no coinciden"
                    }
                    password.length < 6 -> {
                        mensajeError = "La contraseña debe tener al menos 6 caracteres"
                    }
                    else -> {
                        cargando = true
                        mensajeError = ""

                        // 1. Crear usuario en Firebase Authentication
                        auth.createUserWithEmailAndPassword(correo, password)
                            .addOnSuccessListener { authResult ->
                                val uid = authResult.user?.uid ?: ""

                                val nuevoUsuario = Usuario(
                                    uid = uid,
                                    nombre = nombre,
                                    correo = correo,
                                    telefono = telefono,
                                    rol = "CLIENTE"
                                )

                                // 2. Guardar el documento en Firestore usuarios/{uid}
                                db.collection("usuarios").document(uid).set(nuevoUsuario)
                                    .addOnSuccessListener {
                                        cargando = false
                                        onRegisterSuccess("CLIENTE")
                                    }
                                    .addOnFailureListener { e ->
                                        cargando = false
                                        mensajeError = "Error al guardar en Firestore: ${e.localizedMessage}"
                                    }
                            }
                            .addOnFailureListener { e ->
                                cargando = false
                                mensajeError = e.localizedMessage ?: "Error al registrar la cuenta"
                            }
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (cargando) "CREANDO CUENTA..." else "CREAR CUENTA")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }
    }
}
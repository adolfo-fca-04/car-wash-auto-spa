package com.example.carwashautospa.ui.cliente.reserva

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarFechaScreen(
    onFechaSeleccionada: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var fechaTexto by remember { mutableStateOf("") }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val mesFormateado = String.format("%02d", month + 1)
            val diaFormateado = String.format("%02d", dayOfMonth)
            fechaTexto = "$year-$mesFormateado-$diaFormateado"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PASO 3: SELECCIONAR FECHA", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Selecciona el día para tu reserva",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (fechaTexto.isEmpty()) "ELEGIR FECHA" else "FECHA: $fechaTexto")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onFechaSeleccionada(fechaTexto) },
                enabled = fechaTexto.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CONTINUAR A HORARIOS")
            }
        }
    }
}
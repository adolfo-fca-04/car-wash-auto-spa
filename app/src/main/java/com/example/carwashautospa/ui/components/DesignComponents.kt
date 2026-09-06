package com.example.carwashautospa.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carwashautospa.ui.theme.*

// 1. TARJETA ELEVADA ESTÁNDAR
@Composable
fun AppCustomCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// 2. BADGE DE ESTADO CON COLOR E ICONO
@Composable
fun BadgeEstadoAtencion(estado: String) {
    val config: Pair<Color, ImageVector> = when (estado.uppercase()) {
        "RESERVADA", "PENDIENTE" -> Pair(EstadoPendiente, Icons.Default.Info)
        "EN PROCESO" -> Pair(EstadoEnProceso, Icons.Default.DirectionsCar)
        "LISTO PARA RECOGER", "ENTREGADO" -> Pair(EstadoListo, Icons.Default.CheckCircle)
        else -> Pair(EstadoCancelado, Icons.Default.Info)
    }

    val colorFondo = config.first
    val icono = config.second

    Surface(
        color = colorFondo.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorFondo,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = estado,
                color = colorFondo,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

// 3. ANIMACIÓN DE CARGA SUAVE
@Composable
fun LoadingAnimado(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
    }
}
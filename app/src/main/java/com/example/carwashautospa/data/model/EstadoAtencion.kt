package com.example.carwashautospa.data.model

/**
 * Estados oficiales del ciclo de lavado y atención de un vehículo en el Car Wash Auto Spa.
 * Corazón de las reglas de negocio del ciclo de atención.
 */
enum class EstadoAtencion(
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val colorHex: Long
) {
    RESERVADA(
        codigo = "RESERVADA",
        titulo = "Reservada",
        descripcion = "Turno agendado. Esperando llegada del vehículo al taller.",
        colorHex = 0xFF0284C7
    ),
    EN_ESPERA(
        codigo = "EN ESPERA",
        titulo = "En Espera",
        descripcion = "Vehículo recibido en taller, en cola para bahía de lavado.",
        colorHex = 0xFFD97706
    ),
    EN_PROCESO(
        codigo = "EN PROCESO",
        titulo = "En Proceso",
        descripcion = "Vehículo en bahía de lavado activa (lavado, aspirado o secado).",
        colorHex = 0xFF2563EB
    ),
    LISTO_PARA_RECOGER(
        codigo = "LISTO PARA RECOGER",
        titulo = "Listo para Recoger",
        descripcion = "Lavado finalizado e inspeccionado. Listo para entrega.",
        colorHex = 0xFF16A34A
    ),
    ENTREGADO(
        codigo = "ENTREGADO",
        titulo = "Entregado",
        descripcion = "Servicio completado y vehículo retirado por el cliente.",
        colorHex = 0xFF475569
    ),
    CANCELADA(
        codigo = "CANCELADA",
        titulo = "Cancelada",
        descripcion = "Reserva o atención anulada.",
        colorHex = 0xFFDC2626
    );

    /**
     * Máquina de estados: retorna el único siguiente estado permitido en el ciclo de trabajo.
     */
    fun siguienteEstado(): EstadoAtencion? {
        return when (this) {
            RESERVADA -> EN_ESPERA
            EN_ESPERA -> EN_PROCESO
            EN_PROCESO -> LISTO_PARA_RECOGER
            LISTO_PARA_RECOGER -> ENTREGADO
            ENTREGADO, CANCELADA -> null
        }
    }

    companion object {
        fun desdeCodigo(codigo: String?): EstadoAtencion {
            val normalizado = codigo?.trim()?.uppercase() ?: return RESERVADA
            return when (normalizado) {
                "RESERVADA", "RECIBIDO" -> RESERVADA
                "EN ESPERA", "EN_ESPERA" -> EN_ESPERA
                "EN PROCESO", "EN_PROCESO" -> EN_PROCESO
                "LISTO PARA RECOGER", "LISTO", "LISTO_PARA_RECOGER" -> LISTO_PARA_RECOGER
                "ENTREGADO" -> ENTREGADO
                "CANCELADA" -> CANCELADA
                else -> RESERVADA
            }
        }

        val listaFlujoNormal = listOf(
            RESERVADA,
            EN_ESPERA,
            EN_PROCESO,
            LISTO_PARA_RECOGER,
            ENTREGADO
        )
    }
}

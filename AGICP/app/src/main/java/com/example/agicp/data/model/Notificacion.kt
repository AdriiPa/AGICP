package com.example.agicp.data.model

data class Notificacion(
    val id: String = "",
    val tipo: String = "", // "amistad", "torneo", "partido", "sistema"
    val mensaje: String = "",
    val fecha: String = "", // Formato ISO o timestamp serializado
    val leida: Boolean = false,
    val destinatarioId: String = ""
)

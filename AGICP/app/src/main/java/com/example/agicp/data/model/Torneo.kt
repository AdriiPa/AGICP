package com.example.agicp.data.model

data class Torneo(
    val id: String = "",
    val nombre: String = "",
    val maxParticipantes: Int = 0,
    val participantes: Map<String, Boolean> = emptyMap(),
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val fase: String = ""
)

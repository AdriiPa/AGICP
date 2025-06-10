package com.example.agicp.data.model

data class Partido(
    val id: String = "",
    val fechaHora: String = "",
    val pistaId: String = "",
    val participantes: Map<String, Boolean> = emptyMap(),
    val resultado: String? = null
)

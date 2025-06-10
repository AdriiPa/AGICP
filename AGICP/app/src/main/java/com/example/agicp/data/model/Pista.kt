package com.example.agicp.data.model

data class Pista(
    val id: String = "",
    val nombre: String = "",
    val disponible: Boolean = true,
    val partidosAsignados: Map<String, Boolean> = emptyMap()
)

package com.example.agicp.data.model

data class Usuario(
    val id: String = "",
    val nombreUsuario: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val edad: Int = 0,
    val nivel: Int = 1,
    val atributosJugador: String = "",
    val amigos: List<String> = emptyList(),
    val partidosParticipados: List<String> = emptyList(),
    val torneosInscritos: List<String> = emptyList(),
    val email: String = "",
    val rol: String = "jugador",
    val activo: Boolean = false
)

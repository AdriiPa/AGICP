package com.example.agicp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agicp.data.model.Torneo
import com.example.agicp.data.model.Usuario
import com.example.agicp.data.repository.TorneoRepository
import com.example.agicp.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TorneosViewModel : ViewModel() {

    private val torneoRepo = TorneoRepository()
    private val userRepo   = UsuarioRepository()

    // Lista de todos los torneos
    private val _listaTorneos = MutableStateFlow<List<Torneo>>(emptyList())
    val listaTorneos: StateFlow<List<Torneo>> = _listaTorneos

    // Torneo seleccionado (por ejemplo, para edición o vista detalle)
    private val _torneoSeleccionado = MutableStateFlow<Torneo?>(null)
    val torneoSeleccionado: StateFlow<Torneo?> = _torneoSeleccionado

    // --- Usuarios (para inscripciones) ---
    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios

    /** Genera un ID único */
    fun nuevoId(): String = torneoRepo.nuevoId()

    /** Carga todos los torneos */
    fun cargarTorneos() {
        viewModelScope.launch {
            _listaTorneos.value = torneoRepo.obtenerTorneos()
        }
    }

    /** Carga un torneo concreto */
    fun cargarTorneo(id: String) {
        viewModelScope.launch {
            _torneoSeleccionado.value = torneoRepo.obtenerTorneo(id)
        }
    }

    /** Crea un torneo y recarga la lista */
    fun crearTorneo(torneo: Torneo) {
        viewModelScope.launch {
            torneoRepo.crearTorneo(torneo)
            cargarTorneos()
        }
    }

    /** Actualiza un torneo existente y recarga la lista */
    fun actualizarTorneo(torneo: Torneo) {
        viewModelScope.launch {
            torneoRepo.actualizarTorneo(torneo)
            cargarTorneos()
        }
    }

    /** Elimina un torneo y recarga la lista */
    fun eliminarTorneo(torneoId: String) {
        viewModelScope.launch {
            torneoRepo.eliminarTorneo(torneoId)
            cargarTorneos()
        }
    }

    /** Inscribe un jugador en un torneo y recarga la lista */
    fun inscribirJugador(torneoId: String, userId: String) {
        viewModelScope.launch {
            torneoRepo.inscribirJugador(torneoId, userId)
            cargarTorneos()
        }
    }

    /** Cancela la inscripción de un jugador y recarga la lista */
    fun anularInscripcion(torneoId: String, userId: String) {
        viewModelScope.launch {
            torneoRepo.cancelarInscripcion(torneoId, userId)
            cargarTorneos()
        }
    }

    /** Cambia la fase del torneo y recarga la lista */
    fun actualizarFase(torneoId: String, nuevaFase: String) {
        viewModelScope.launch {
            torneoRepo.actualizarFase(torneoId, nuevaFase)
            cargarTorneos()
        }
    }

    /** Carga todos los usuarios (callback) */
    fun cargarUsuarios() {
        userRepo.obtenerTodosLosUsuarios { usuarios ->
            _listaUsuarios.value = usuarios
        }
    }

    /** Limpia el torneo seleccionado */
    fun clearSeleccionado() {
        _torneoSeleccionado.value = null
    }
}

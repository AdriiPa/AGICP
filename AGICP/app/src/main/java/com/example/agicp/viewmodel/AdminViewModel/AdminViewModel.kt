package com.example.agicp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agicp.data.model.Pista
import com.example.agicp.data.model.Torneo
import com.example.agicp.data.model.Partido
import com.example.agicp.data.model.Usuario
import com.example.agicp.data.repository.PistaRepository
import com.example.agicp.data.repository.TorneoRepository
import com.example.agicp.data.repository.PartidoRepository
import com.example.agicp.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    // Repositorios
    private val usuarioRepo = UsuarioRepository()
    private val pistaRepo = PistaRepository()
    private val torneoRepo = TorneoRepository()
    private val partidoRepo = PartidoRepository()

    // Flows de estado
    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios

    private val _listaPistas = MutableStateFlow<List<Pista>>(emptyList())
    val listaPistas: StateFlow<List<Pista>> = _listaPistas

    private val _listaTorneos = MutableStateFlow<List<Torneo>>(emptyList())
    val listaTorneos: StateFlow<List<Torneo>> = _listaTorneos

    private val _listaPartidos = MutableStateFlow<List<Partido>>(emptyList())
    val listaPartidos: StateFlow<List<Partido>> = _listaPartidos

    // --- USUARIOS ---

    fun cargarUsuarios() {
        usuarioRepo.obtenerTodosLosUsuarios { usuarios ->
            _listaUsuarios.value = usuarios
        }
    }

    fun activarUsuario(usuarioId: String) {
        usuarioRepo.obtenerUsuarioPorId(usuarioId) { usuario ->
            if (usuario != null) {
                usuarioRepo.actualizarUsuario(usuario.copy(activo = true))
                cargarUsuarios()
            }
        }
    }

    fun cambiarRolUsuario(usuarioId: String, nuevoRol: String) {
        usuarioRepo.obtenerUsuarioPorId(usuarioId) { usuario ->
            if (usuario != null) {
                usuarioRepo.actualizarUsuario(usuario.copy(rol = nuevoRol))
                cargarUsuarios()
            }
        }
    }

    fun eliminarUsuario(usuarioId: String) {
        usuarioRepo.obtenerUsuarioPorId(usuarioId) { usuario ->
            if (usuario != null) {
                usuarioRepo.actualizarUsuario(usuario.copy(activo = false))
                cargarUsuarios()
            }
        }
    }

    // --- PISTAS ---

    fun cargarPistas() {
        viewModelScope.launch {
            _listaPistas.value = pistaRepo.obtenerPistas()
        }
    }

    fun agregarPista(pista: Pista) {
        viewModelScope.launch {
            pistaRepo.agregarPista(pista)
            cargarPistas()
        }
    }

    fun eliminarPista(pistaId: String) {
        viewModelScope.launch {
            pistaRepo.eliminarPista(pistaId)
            cargarPistas()
        }
    }

    fun cambiarDisponibilidadPista(pistaId: String, disponible: Boolean) {
        viewModelScope.launch {
            pistaRepo.cambiarDisponibilidad(pistaId, disponible)
            cargarPistas()
        }
    }

    fun asignarPartidoAPista(pistaId: String, partidoId: String) {
        viewModelScope.launch {
            pistaRepo.asignarPartido(pistaId, partidoId)
            cargarPistas()
        }
    }

    fun desasignarPartidoDePista(pistaId: String, partidoId: String) {
        viewModelScope.launch {
            pistaRepo.desasignarPartido(pistaId, partidoId)
            cargarPistas()
        }
    }

    // --- TORNEOS ---

    fun cargarTorneos() {
        viewModelScope.launch {
            _listaTorneos.value = torneoRepo.obtenerTorneos()
        }
    }

    fun crearTorneo(torneo: Torneo) {
        viewModelScope.launch {
            torneoRepo.crearTorneo(torneo)
            cargarTorneos()
        }
    }

    fun eliminarTorneo(torneoId: String) {
        viewModelScope.launch {
            torneoRepo.eliminarTorneo(torneoId)
            cargarTorneos()
        }
    }

    fun actualizarFaseTorneo(torneoId: String, fase: String) {
        viewModelScope.launch {
            torneoRepo.actualizarFase(torneoId, fase)
            cargarTorneos()
        }
    }

    // --- PARTIDOS ---

    fun cargarPartidos() {
        viewModelScope.launch {
            _listaPartidos.value = partidoRepo.obtenerPartidos()
        }
    }

    fun crearPartido(partido: Partido) {
        viewModelScope.launch {
            partidoRepo.crearPartido(partido)
            cargarPartidos()
        }
    }

    fun eliminarPartido(partidoId: String) {
        viewModelScope.launch {
            partidoRepo.eliminarPartido(partidoId)
            cargarPartidos()
        }
    }

    fun actualizarResultadoPartido(partidoId: String, resultado: String) {
        viewModelScope.launch {
            partidoRepo.actualizarResultado(partidoId, resultado)
            cargarPartidos()
        }
    }

}

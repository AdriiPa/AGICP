package com.example.agicp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.agicp.data.model.Usuario
import com.example.agicp.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UsuarioViewModel : ViewModel() {

    private val repository = UsuarioRepository()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios: StateFlow<List<Usuario>> = _listaUsuarios

    private val _usuarioEncontrado = MutableStateFlow<Usuario?>(null)
    val usuarioEncontrado: StateFlow<Usuario?> = _usuarioEncontrado

    fun registrarUsuario(usuario: Usuario) {
        repository.registrarUsuario(usuario)
        _usuarioActual.value = usuario
    }

    fun cargarUsuario(id: String) {
        repository.obtenerUsuarioPorId(id) {
            _usuarioActual.value = it
        }
    }

    fun cargarTodosLosUsuarios() {
        repository.obtenerTodosLosUsuarios {
            _listaUsuarios.value = it
        }
    }

    fun actualizarUsuario(usuario: Usuario) {
        repository.actualizarUsuario(usuario)
        _usuarioActual.value = usuario
    }

    fun modificarStatsUsuario(id: String, nivel: Int, atributos: String) {
        repository.modificarStatsUsuario(id, nivel, atributos)
    }

    fun agregarAmigo(idUsuario: String, idAmigo: String) {
        repository.agregarAmigo(idUsuario, idAmigo)
    }

    fun eliminarAmigo(idUsuario: String, idAmigo: String) {
        repository.eliminarAmigo(idUsuario, idAmigo)
    }

    fun apuntarAPartido(idUsuario: String, partidoId: String) {
        repository.participarEnPartido(idUsuario, partidoId)
    }

    fun inscribirseATorneo(idUsuario: String, torneoId: String) {
        repository.inscribirseEnTorneo(idUsuario, torneoId)
    }

    fun activarUsuario(idUsuario: String) {
        repository.actualizarEstado(idUsuario, true)
    }

    fun eliminarUsuario(idUsuario: String) {
        repository.eliminarUsuario(idUsuario)
    }

    fun buscarUsuarioPorNombreUsuario(nombreUsuario: String) {
        repository.buscarUsuarioPorNombreUsuario(nombreUsuario) {
            _usuarioEncontrado.value = it
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}

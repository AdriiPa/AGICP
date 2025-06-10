package com.example.agicp.viewmodel.torneosViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agicp.data.model.Torneo
import com.example.agicp.data.repository.TorneoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TorneosViewModel : ViewModel() {

    private val repo = TorneoRepository()

    private val _listaTorneos = MutableStateFlow<List<Torneo>>(emptyList())
    val listaTorneos: StateFlow<List<Torneo>> = _listaTorneos

    private val _torneoSeleccionado = MutableStateFlow<Torneo?>(null)
    val torneoSeleccionado: StateFlow<Torneo?> = _torneoSeleccionado

    fun cargarTorneos() {
        viewModelScope.launch {
            _listaTorneos.value = repo.obtenerTorneos()
        }
    }

    fun crearTorneo(torneo: Torneo) {
        viewModelScope.launch {
            repo.crearTorneo(torneo)
            cargarTorneos()
        }
    }

    fun eliminarTorneo(torneoId: String) {
        viewModelScope.launch {
            repo.eliminarTorneo(torneoId)
            cargarTorneos()
        }
    }

    fun actualizarFase(torneoId: String, fase: String) {
        viewModelScope.launch {
            repo.actualizarFase(torneoId, fase)
            cargarTorneos()
        }
    }

    fun inscribirJugador(torneoId: String, userId: String) {
        viewModelScope.launch {
            repo.inscribirJugador(torneoId, userId)
            cargarTorneos()
        }
    }

    fun cancelarInscripcion(torneoId: String, userId: String) {
        viewModelScope.launch {
            repo.cancelarInscripcion(torneoId, userId)
            cargarTorneos()
        }
    }
}

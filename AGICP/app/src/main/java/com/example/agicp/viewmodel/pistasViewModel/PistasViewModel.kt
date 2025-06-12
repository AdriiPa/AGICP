package com.example.agicp.viewmodel.pistasViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agicp.data.model.Pista
import com.example.agicp.data.repository.PistaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PistasViewModel : ViewModel() {

    private val repo = PistaRepository()

    private val _listaPistas = MutableStateFlow<List<Pista>>(emptyList())
    val listaPistas: StateFlow<List<Pista>> = _listaPistas

    private val _pistaSeleccionada = MutableStateFlow<Pista?>(null)
    val pistaSeleccionada: StateFlow<Pista?> = _pistaSeleccionada

    private val _partidosAsignados = MutableStateFlow<List<String>>(emptyList())
    val partidosAsignados: StateFlow<List<String>> = _partidosAsignados

    init {
        cargarTodasLasPistas()
    }

    /** Carga todas las pistas (disponibles o no) */
    fun cargarTodasLasPistas() {
        viewModelScope.launch {
            _listaPistas.value = repo.obtenerPistas()
        }
    }

    /** Carga sólo las pistas actualmente disponibles */
    fun cargarPistasDisponibles() {
        viewModelScope.launch {
            _listaPistas.value = repo.obtenerPistasDisponibles()
        }
    }

    /** Selecciona una pista por su ID y carga sus datos */
    fun seleccionarPista(pistaId: String) {
        viewModelScope.launch {
            val p = repo.obtenerPistaPorId(pistaId)
            _pistaSeleccionada.value = p
            _partidosAsignados.value = p
                ?.let { repo.obtenerPartidosAsignados(it.id) }
                ?: emptyList()
        }
    }

    /** Prepara un nuevo objeto Pista con ID generado */
    fun nuevaPistaTemplate(nombre: String): Pista {
        val id = repo.nuevoId()
        return Pista(id = id, nombre = nombre, disponible = true, partidosAsignados = emptyMap())
    }

    /** Crea la pista y vuelve a recargar el listado */
    fun crearPista(pista: Pista) {
        viewModelScope.launch {
            repo.crearPista(pista)
            cargarTodasLasPistas()
        }
    }

    /** Actualiza todos los campos de la pista seleccionada */
    fun actualizarPista(pista: Pista) {
        viewModelScope.launch {
            repo.actualizarPista(pista)
            cargarTodasLasPistas()
            _pistaSeleccionada.value = pista
        }
    }

    /** Elimina una pista y refresca la lista */
    fun eliminarPista(pistaId: String) {
        viewModelScope.launch {
            repo.eliminarPista(pistaId)
            cargarTodasLasPistas()
            if (_pistaSeleccionada.value?.id == pistaId) {
                _pistaSeleccionada.value = null
                _partidosAsignados.value = emptyList()
            }
        }
    }

    /** Cambia la disponibilidad y refresca */
    fun cambiarDisponibilidad(pistaId: String, disponible: Boolean) {
        viewModelScope.launch {
            repo.cambiarDisponibilidad(pistaId, disponible)
            cargarTodasLasPistas()
            _pistaSeleccionada.value = _pistaSeleccionada.value
                ?.copy(disponible = disponible)
        }
    }

    /** Asigna un partido a la pista (si no supera 4 inscritos) */
    fun asignarPartido(pistaId: String, partidoId: String) {
        viewModelScope.launch {
            // impide más de 4
            val actuales = repo.obtenerPartidosAsignados(pistaId)
            if (actuales.size < 4) {
                repo.asignarPartido(pistaId, partidoId)
                _partidosAsignados.value = repo.obtenerPartidosAsignados(pistaId)
            }
        }
    }

    /** Desasigna un partido de la pista */
    fun desasignarPartido(pistaId: String, partidoId: String) {
        viewModelScope.launch {
            repo.desasignarPartido(pistaId, partidoId)
            _partidosAsignados.value = repo.obtenerPartidosAsignados(pistaId)
        }
    }
}

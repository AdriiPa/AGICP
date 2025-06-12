package com.example.agicp.viewmodel.partidosViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agicp.data.model.Partido
import com.example.agicp.data.repository.PartidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PartidosViewModel : ViewModel() {

    private val repo = PartidoRepository()

    private val _listaPartidos = MutableStateFlow<List<Partido>>(emptyList())
    val listaPartidos: StateFlow<List<Partido>> = _listaPartidos

    private val _partidoSeleccionado = MutableStateFlow<Partido?>(null)
    val partidoSeleccionado: StateFlow<Partido?> = _partidoSeleccionado

    fun cargarPartidos() {
        viewModelScope.launch {
            _listaPartidos.value = repo.obtenerPartidos()
        }
    }

    fun cargarPartidosPorFecha(fecha: String) {
        viewModelScope.launch {
            _listaPartidos.value = repo.obtenerPartidosPorFecha(fecha)
        }
    }

    fun crearPartido(partido: Partido) {
        viewModelScope.launch {
            repo.crearPartido(partido)
            cargarPartidos()
        }
    }

    fun eliminarPartido(partidoId: String) {
        viewModelScope.launch {
            repo.eliminarPartido(partidoId)
            cargarPartidos()
        }
    }

    fun actualizarResultado(partidoId: String, resultado: String) {
        viewModelScope.launch {
            repo.actualizarResultado(partidoId, resultado)
            cargarPartidos()
        }
    }

    fun unirseAPartido(partidoId: String, userId: String) {
        viewModelScope.launch {
            repo.unirseAPartido(partidoId, userId)
            cargarPartidos()
        }
    }

    fun salirDePartido(partidoId: String, userId: String) {
        viewModelScope.launch {
            repo.salirDePartido(partidoId, userId)
            cargarPartidos()
        }
    }
    fun getEstadisticasUsuario(userId: String): Triple<Int, Int, Int> {
        val todos = listaPartidos.value
        val jugados = todos.filter { it.participantes.containsKey(userId) }
        val ganados = jugados.count { it.resultado?.contains("gana:$userId") == true }
        val perdidos = jugados.count { it.resultado?.startsWith("gana:") == true && !it.resultado.contains(userId) }
        return Triple(jugados.size, ganados, perdidos)
    }


}

package com.example.agicp.data.repository

import com.example.agicp.data.model.Partido
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class PartidoRepository {
    private val ref = FirebaseDatabase.getInstance().getReference("partidos")

    suspend fun obtenerPartidos(): List<Partido> {
        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.getValue(Partido::class.java) }
    }

    suspend fun obtenerPartidosPorFecha(fecha: String): List<Partido> {
        val snapshot = ref.get().await()
        return snapshot.children
            .mapNotNull { it.getValue(Partido::class.java) }
            .filter { it.fechaHora.startsWith(fecha) }
    }

    suspend fun unirseAPartido(partidoId: String, userId: String) {
        ref.child(partidoId).child("participantes").child(userId).setValue(true).await()
    }

    suspend fun salirDePartido(partidoId: String, userId: String) {
        ref.child(partidoId).child("participantes").child(userId).removeValue().await()
    }

    suspend fun crearPartido(partido: Partido) {
        ref.child(partido.id).setValue(partido).await()
    }

    suspend fun actualizarResultado(partidoId: String, resultado: String) {
        ref.child(partidoId).child("resultado").setValue(resultado).await()
    }

    suspend fun eliminarPartido(partidoId: String) {
        ref.child(partidoId).removeValue().await()
    }

}
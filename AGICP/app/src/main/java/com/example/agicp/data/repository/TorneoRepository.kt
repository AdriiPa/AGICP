package com.example.agicp.data.repository

import com.example.agicp.data.model.Torneo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class TorneoRepository {
    private val ref = FirebaseDatabase.getInstance().getReference("torneos")

    suspend fun obtenerTorneos(): List<Torneo> {
        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.getValue(Torneo::class.java) }
    }

    suspend fun inscribirJugador(torneoId: String, userId: String) {
        ref.child(torneoId).child("participantes").child(userId).setValue(true).await()
    }

    suspend fun crearTorneo(torneo: Torneo) {
        ref.child(torneo.id).setValue(torneo).await()
    }

    suspend fun cancelarInscripcion(torneoId: String, userId: String) {
        ref.child(torneoId).child("participantes").child(userId).removeValue().await()
    }
    suspend fun actualizarFase(torneoId: String, nuevaFase: String) {
        ref.child(torneoId).child("fase").setValue(nuevaFase).await()
    }

    suspend fun eliminarTorneo(torneoId: String) {
        ref.child(torneoId).removeValue().await()
    }

}
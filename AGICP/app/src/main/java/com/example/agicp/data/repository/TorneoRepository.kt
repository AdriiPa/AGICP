package com.example.agicp.data.repository

import com.example.agicp.data.model.Torneo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.*

class TorneoRepository {
    private val ref = FirebaseDatabase.getInstance().getReference("torneos")

    /** Devuelve la lista completa de torneos */
    suspend fun obtenerTorneos(): List<Torneo> {
        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.getValue(Torneo::class.java) }
    }

    /** Devuelve un torneo concreto por su ID */
    suspend fun obtenerTorneo(torneoId: String): Torneo? {
        val snapshot = ref.child(torneoId).get().await()
        return snapshot.getValue(Torneo::class.java)
    }

    /** Genera un ID único para un torneo nuevo */
    fun nuevoId(): String = ref.push().key ?: UUID.randomUUID().toString()

    /** Crea un torneo — usa el ID ya presente en el objeto */
    suspend fun crearTorneo(torneo: Torneo) {
        ref.child(torneo.id).setValue(torneo).await()
    }

    /** Actualiza un torneo completo (nombre, fechas, maxParticipantes, fase, inscritos...) */
    suspend fun actualizarTorneo(torneo: Torneo) {
        ref.child(torneo.id).setValue(torneo).await()
    }

    /** Inscribe a un jugador en el torneo */
    suspend fun inscribirJugador(torneoId: String, userId: String) {
        ref.child(torneoId).child("participantes").child(userId).setValue(true).await()
    }

    /** Cancela la inscripción de un jugador */
    suspend fun cancelarInscripcion(torneoId: String, userId: String) {
        ref.child(torneoId).child("participantes").child(userId).removeValue().await()
    }

    /** Actualiza únicamente la fase del torneo */
    suspend fun actualizarFase(torneoId: String, nuevaFase: String) {
        ref.child(torneoId).child("fase").setValue(nuevaFase).await()
    }

    /** Elimina un torneo completo */
    suspend fun eliminarTorneo(torneoId: String) {
        ref.child(torneoId).removeValue().await()
    }
}

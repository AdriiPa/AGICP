package com.example.agicp.data.repository

import com.example.agicp.data.model.Pista
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.*

class PistaRepository {
    private val ref = FirebaseDatabase.getInstance().getReference("pistas")

    /** Genera un ID nuevo para una pista */
    fun nuevoId(): String = ref.push().key ?: UUID.randomUUID().toString()

    /** Lee todas las pistas */
    suspend fun obtenerPistas(): List<Pista> {
        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.getValue(Pista::class.java) }
    }

    /** Lee solo las pistas disponibles */
    suspend fun obtenerPistasDisponibles(): List<Pista> {
        val snapshot = ref.orderByChild("disponible").equalTo(true).get().await()
        return snapshot.children.mapNotNull { it.getValue(Pista::class.java) }
    }

    /** Lee una única pista por su ID */
    suspend fun obtenerPistaPorId(pistaId: String): Pista? {
        val snap = ref.child(pistaId).get().await()
        return snap.getValue(Pista::class.java)
    }

    /** Crea una nueva pista */
    suspend fun crearPista(pista: Pista) {
        ref.child(pista.id).setValue(pista).await()
    }

    /** Actualiza todos los campos de una pista */
    suspend fun actualizarPista(pista: Pista) {
        ref.child(pista.id).setValue(pista).await()
    }

    /** Cambia únicamente la disponibilidad de la pista */
    suspend fun cambiarDisponibilidad(pistaId: String, disponible: Boolean) {
        ref.child(pistaId).child("disponible").setValue(disponible).await()
    }

    /** Elimina la pista completamente */
    suspend fun eliminarPista(pistaId: String) {
        ref.child(pistaId).removeValue().await()
    }

    /** Asigna un partido a la pista (dentro de partidosAsignados) */
    suspend fun asignarPartido(pistaId: String, partidoId: String) {
        ref.child(pistaId).child("partidosAsignados").child(partidoId).setValue(true).await()
    }

    /** Desasigna ese partido de la pista */
    suspend fun desasignarPartido(pistaId: String, partidoId: String) {
        ref.child(pistaId).child("partidosAsignados").child(partidoId).removeValue().await()
    }

    /** Obtiene la lista de IDs de los partidos asignados a esa pista */
    suspend fun obtenerPartidosAsignados(pistaId: String): List<String> {
        val snap = ref.child(pistaId).child("partidosAsignados").get().await()
        return snap.children.mapNotNull { it.key }
    }

    /** Vacía por completo todos los partidos asignados (uso interno o de reset) */
    suspend fun limpiarPartidosAsignados(pistaId: String) {
        ref.child(pistaId).child("partidosAsignados").removeValue().await()
    }
}

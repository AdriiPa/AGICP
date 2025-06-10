package com.example.agicp.data.repository

import com.example.agicp.data.model.Pista
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class PistaRepository {
    private val ref = FirebaseDatabase.getInstance().getReference("pistas")

    suspend fun obtenerPistas(): List<Pista> {
        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.getValue(Pista::class.java) }
    }

    suspend fun agregarPista(pista: Pista) {
        ref.child(pista.id).setValue(pista).await()
    }

    suspend fun eliminarPista(pistaId: String) {
        ref.child(pistaId).removeValue().await()
    }

    suspend fun cambiarDisponibilidad(pistaId: String, disponible: Boolean) {
        ref.child(pistaId).child("disponible").setValue(disponible).await()
    }

    suspend fun asignarPartido(pistaId: String, partidoId: String) {
        ref.child(pistaId).child("partidosAsignados").child(partidoId).setValue(true).await()
    }
    suspend fun desasignarPartido(pistaId: String, partidoId: String) {
        ref.child(pistaId).child("partidosAsignados").child(partidoId).removeValue().await()
    }


}
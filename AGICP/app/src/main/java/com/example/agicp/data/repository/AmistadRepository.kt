package com.example.agicp.data.repository

import com.example.agicp.data.model.Amistad
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AmistadRepository {
    private val ref = FirebaseDatabase.getInstance().getReference("amistades")

    suspend fun enviarSolicitud(idUsuario: String, idAmigo: String) {
        val solicitud = Amistad(idUsuario, idAmigo, "pendiente")
        ref.push().setValue(solicitud).await()
    }

    suspend fun aceptarSolicitud(amistadId: String) {
        ref.child(amistadId).child("estado").setValue("aceptado").await()
    }

    suspend fun rechazarSolicitud(amistadId: String) {
        ref.child(amistadId).child("estado").setValue("rechazado").await()
    }

    suspend fun obtenerSolicitudes(idUsuario: String): List<Amistad> {
        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.getValue(Amistad::class.java) }
            .filter { it.idAmigo == idUsuario && it.estado == "pendiente" }
    }

    suspend fun eliminarAmistad(amistadId: String) {
        ref.child(amistadId).removeValue().await()
    }

}
package com.example.agicp.data.repository

import com.example.agicp.data.model.Notificacion
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class NotificacionRepository {
    private val ref = FirebaseDatabase.getInstance().getReference("notificaciones")

    suspend fun enviarNotificacion(notificacion: Notificacion) {
        ref.child(notificacion.id).setValue(notificacion).await()
    }

    suspend fun obtenerNotificaciones(destinatarioId: String): List<Notificacion> {
        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { it.getValue(Notificacion::class.java) }
            .filter { it.destinatarioId == destinatarioId }
    }

    suspend fun marcarComoLeida(notificacionId: String) {
        ref.child(notificacionId).child("leida").setValue(true).await()
    }
    suspend fun eliminarNotificacion(notificacionId: String) {
        ref.child(notificacionId).removeValue().await()
    }
}
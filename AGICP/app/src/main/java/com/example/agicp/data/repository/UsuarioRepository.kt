package com.example.agicp.data.repository

import com.example.agicp.data.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class UsuarioRepository {

    private val firestore = FirebaseFirestore.getInstance().collection("usuarios")

    fun registrarUsuario(usuario: Usuario) {
        usuario.id?.let {
            firestore.document(it).set(usuario)
        }
    }

    fun obtenerUsuarioPorId(id: String, callback: (Usuario?) -> Unit) {
        firestore.document(id).get().addOnSuccessListener { snapshot ->
            callback(snapshot.toObject(Usuario::class.java))
        }.addOnFailureListener { callback(null) }
    }

    fun obtenerTodosLosUsuarios(callback: (List<Usuario>) -> Unit) {
        firestore.get().addOnSuccessListener { result ->
            val usuarios = result.mapNotNull { it.toObject(Usuario::class.java) }
            callback(usuarios)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }

    fun actualizarUsuario(usuario: Usuario) {
        usuario.id?.let {
            firestore.document(it).set(usuario)
        }
    }

    fun modificarStatsUsuario(id: String, nivel: Int, atributosJugador: String) {
        firestore.document(id).update(
            mapOf(
                "nivel" to nivel,
                "atributosJugador" to atributosJugador
            )
        )
    }

    fun agregarAmigo(idUsuario: String, idAmigo: String) {
        firestore.document(idUsuario).update("amigos", com.google.firebase.firestore.FieldValue.arrayUnion(idAmigo))
        firestore.document(idAmigo).update("amigos", com.google.firebase.firestore.FieldValue.arrayUnion(idUsuario))
    }

    fun eliminarAmigo(idUsuario: String, idAmigo: String) {
        firestore.document(idUsuario).update("amigos", com.google.firebase.firestore.FieldValue.arrayRemove(idAmigo))
        firestore.document(idAmigo).update("amigos", com.google.firebase.firestore.FieldValue.arrayRemove(idUsuario))
    }

    fun participarEnPartido(idUsuario: String, partidoId: String) {
        firestore.document(idUsuario).update("partidosParticipados", com.google.firebase.firestore.FieldValue.arrayUnion(partidoId))
    }

    fun inscribirseEnTorneo(idUsuario: String, torneoId: String) {
        firestore.document(idUsuario).update("torneosInscritos", com.google.firebase.firestore.FieldValue.arrayUnion(torneoId))
    }

    fun actualizarEstado(idUsuario: String, activo: Boolean) {
        firestore.document(idUsuario).update("activo", activo)
    }

    fun eliminarUsuario(idUsuario: String) {
        firestore.document(idUsuario).delete()
    }

    fun buscarUsuarioPorNombreUsuario(nombreUsuario: String, callback: (Usuario?) -> Unit) {
        firestore.whereEqualTo("nombreUsuario", nombreUsuario)
            .get()
            .addOnSuccessListener { result ->
                val usuario = result.firstOrNull()?.toObject(Usuario::class.java)
                callback(usuario)
            }
            .addOnFailureListener { callback(null) }
    }
}

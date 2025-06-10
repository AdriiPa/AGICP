package com.example.agicp.data.repository

import com.example.agicp.data.model.Usuario
import com.google.firebase.database.*

class UsuarioRepository {

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("usuarios")

    fun registrarUsuario(usuario: Usuario) {
        dbRef.child(usuario.id).setValue(usuario)
    }

    fun obtenerUsuarioPorId(id: String, callback: (Usuario?) -> Unit) {
        dbRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                callback(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun obtenerTodosLosUsuarios(callback: (List<Usuario>) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuarios = mutableListOf<Usuario>()
                snapshot.children.mapNotNullTo(usuarios) {
                    it.getValue(Usuario::class.java)
                }
                callback(usuarios)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun actualizarUsuario(usuario: Usuario) {
        dbRef.child(usuario.id).setValue(usuario)
    }

    fun modificarStatsUsuario(id: String, nivel: Int, atributosJugador: String) {
        val updates = mapOf(
            "nivel" to nivel,
            "atributosJugador" to atributosJugador
        )
        dbRef.child(id).updateChildren(updates)
    }

    fun agregarAmigo(idUsuario: String, idAmigo: String) {
        dbRef.child(idUsuario).child("amigos").child(idAmigo).setValue(true)
        dbRef.child(idAmigo).child("amigos").child(idUsuario).setValue(true)
    }

    fun eliminarAmigo(idUsuario: String, idAmigo: String) {
        dbRef.child(idUsuario).child("amigos").child(idAmigo).removeValue()
        dbRef.child(idAmigo).child("amigos").child(idUsuario).removeValue()
    }

    fun participarEnPartido(idUsuario: String, partidoId: String) {
        dbRef.child(idUsuario).child("partidosParticipados").child(partidoId).setValue(true)
    }

    fun inscribirseEnTorneo(idUsuario: String, torneoId: String) {
        dbRef.child(idUsuario).child("torneosInscritos").child(torneoId).setValue(true)
    }

    fun actualizarEstado(idUsuario: String, activo: Boolean) {
        dbRef.child(idUsuario).child("activo").setValue(activo)
    }

    fun eliminarUsuario(idUsuario: String) {
        dbRef.child(idUsuario).removeValue()
    }

    fun buscarUsuarioPorNombreUsuario(nombreUsuario: String, callback: (Usuario?) -> Unit) {
        dbRef.orderByChild("nombreUsuario").equalTo(nombreUsuario)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usuario = snapshot.children.firstOrNull()?.getValue(Usuario::class.java)
                    callback(usuario)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }
}

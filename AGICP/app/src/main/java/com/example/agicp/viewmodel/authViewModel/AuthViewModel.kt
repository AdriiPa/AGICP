package com.example.agicp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.agicp.R
import com.example.agicp.data.model.Usuario
import com.example.agicp.core.navigation.Rutas
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance().collection("usuarios")
    private val TAG = "AuthViewModel"

    val isLoading = MutableStateFlow(false)
    val loginSuccess = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)
    val userNotActivated = MutableStateFlow(false)
    val userRole = MutableStateFlow<String?>(null)
    private val loginGoogleSuccess = MutableStateFlow(false)

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    fun loginWithEmail(
        email: String,
        password: String,
        navController: NavController,
        usuarioViewModel: UsuarioViewModel
    ) {
        isLoading.value = true
        errorMessage.value = null
        loginSuccess.value = false
        userNotActivated.value = false

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            isLoading.value = false
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    firestore.document(userId).get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val usuario = document.toObject(Usuario::class.java)
                            if (usuario?.activo == true) {
                                userRole.value = usuario.rol
                                loginSuccess.value = true
                                usuarioViewModel.cargarUsuario(userId)

                                when (usuario.rol) {
                                    "admin" -> navController.navigate(Rutas.ADMIN) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                    else -> navController.navigate(Rutas.DASHBOARD) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                }
                            } else {
                                auth.signOut()
                                userNotActivated.value = true
                            }
                        } else {
                            errorMessage.value = "Usuario no encontrado en Firestore."
                            auth.signOut()
                        }
                    }.addOnFailureListener {
                        errorMessage.value = "Error al verificar usuario."
                    }
                }
            } else {
                val exception = task.exception
                when {
                    exception is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
                        errorMessage.value = "Las credenciales son inválidas."
                    }
                    exception is com.google.firebase.auth.FirebaseAuthInvalidUserException -> {
                        errorMessage.value = "El usuario no existe."
                    }
                    else -> {
                        errorMessage.value = "Error desconocido."
                    }
                }
            }
        }
    }

    fun loginWithGoogle(
        idToken: String, navController: NavController, usuarioViewModel: UsuarioViewModel
    ) {
        Log.d(TAG, "Iniciando sesión con Google... ID Token: $idToken")
        isLoading.value = true
        errorMessage.value = null
        loginSuccess.value = false
        userNotActivated.value = false

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            isLoading.value = false
            if (task.isSuccessful) {
                val usuarioId = auth.currentUser?.uid
                val usuarioEmail = auth.currentUser?.email

                if (usuarioId != null && usuarioEmail != null) {
                    firestore.document(usuarioId).get().addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val nuevoUsuario = Usuario(
                                id = usuarioId,
                                nombreUsuario = "",
                                nombre = "",
                                apellido = "",
                                email = usuarioEmail,
                                edad = 0,
                                nivel = 1,
                                atributosJugador = "",
                                amigos = emptyList(),
                                partidosParticipados = emptyList(),
                                torneosInscritos = emptyList(),
                                rol = "jugador",
                                activo = false
                            )

                            firestore.document(usuarioId).set(nuevoUsuario).addOnSuccessListener {
                                userNotActivated.value = true
                                navController.navigate(Rutas.REGISTRO)
                            }
                        } else {
                            val usuario = document.toObject(Usuario::class.java)
                            if (usuario?.activo == true) {
                                userRole.value = usuario.rol
                                loginSuccess.value = true
                                loginGoogleSuccess.value = true
                                usuarioViewModel.cargarUsuario(usuarioId)

                                when (usuario.rol) {
                                    "admin" -> navController.navigate(Rutas.ADMIN) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                    else -> navController.navigate(Rutas.DASHBOARD) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                }
                            } else {
                                auth.signOut()
                                userNotActivated.value = true
                                navController.navigate(Rutas.REGISTRO)
                            }
                        }
                    }.addOnFailureListener {
                        errorMessage.value = "Error al verificar usuario en Firestore."
                    }
                } else {
                    errorMessage.value = "Error al obtener los datos del usuario."
                }
            } else {
                errorMessage.value = task.exception?.message ?: "Error al iniciar sesión con Google"
            }
        }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        nombreUsuario: String,
        edad: Int,
        nivel: Int,
        atributosJugador: String,
        navController: NavController
    ) {
        isLoading.value = true
        errorMessage.value = null
        loginSuccess.value = false

        firestore.whereEqualTo("nombreUsuario", nombreUsuario).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    isLoading.value = false
                    errorMessage.value = "El nombre de usuario ya está en uso."
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading.value = false
                            if (task.isSuccessful) {
                                val uid = auth.currentUser?.uid
                                if (uid != null) {
                                    val nuevoUsuario = Usuario(
                                        id = uid,
                                        email = email,
                                        nombre = nombre,
                                        apellido = apellido,
                                        nombreUsuario = nombreUsuario,
                                        edad = edad,
                                        nivel = nivel,
                                        atributosJugador = atributosJugador,
                                        amigos = emptyList(),
                                        partidosParticipados = emptyList(),
                                        torneosInscritos = emptyList(),
                                        rol = "jugador",
                                        activo = false
                                    )
                                    firestore.document(uid).set(nuevoUsuario).addOnSuccessListener {
                                        userNotActivated.value = true
                                        navController.navigate(Rutas.LOGIN) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }.addOnFailureListener {
                                        errorMessage.value = "Error al guardar usuario: ${it.message}"
                                    }
                                }
                            } else {
                                errorMessage.value = task.exception?.message ?: "Error desconocido"
                            }
                        }
                }
            }.addOnFailureListener {
                isLoading.value = false
                errorMessage.value = "Error al verificar nombre de usuario."
            }
    }

    fun signOut(
        context: Context, navController: NavHostController, usuarioViewModel: UsuarioViewModel
    ) {
        auth.signOut()
        usuarioViewModel.cerrarSesion()
        navController.navigate(Rutas.LOGIN) {
            popUpTo(0)
            launchSingleTop = true
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}

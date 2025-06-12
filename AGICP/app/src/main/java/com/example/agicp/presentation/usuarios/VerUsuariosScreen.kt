package com.example.agicp.presentation.usuarios

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agicp.data.model.Usuario
import com.example.agicp.viewmodel.AuthViewModel
import com.example.agicp.viewmodel.UsuarioViewModel

@Composable
fun VerUsuariosScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel,
    authViewModel: AuthViewModel
) {
    val usuariosList by usuarioViewModel.listaUsuarios.collectAsState(initial = emptyList())
    val currentUserId = authViewModel.getCurrentUserId()

    // Filtrar el usuario actual para no mostrarlo en la lista
    val filteredUsuarios = usuariosList.filter { it.id != currentUserId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Usuarios Registrados",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(filteredUsuarios) { usuario ->
                    UserCard(
                        usuario = usuario,
                        onActivate = {
                            usuarioViewModel.activarUsuario(usuario.id)
                        },
                        onDelete = {
                            usuarioViewModel.eliminarUsuario(usuario.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    usuario: Usuario,
    onActivate: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${usuario.nombre} ${usuario.apellido}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Correo: ${usuario.email}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Nivel: ${usuario.nivel}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onActivate()
                        Toast
                            .makeText(context, "Usuario ${usuario.nombre} activado", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Activar")
                }
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        onDelete()
                        Toast
                            .makeText(context, "Usuario ${usuario.nombre} eliminado", Toast.LENGTH_SHORT)
                            .show()
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

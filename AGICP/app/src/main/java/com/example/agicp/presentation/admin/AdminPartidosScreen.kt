package com.example.agicp.presentation.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agicp.data.model.Partido
import com.example.agicp.data.model.Usuario
import com.example.agicp.viewmodel.UsuarioViewModel
import com.example.agicp.viewmodel.partidosViewModel.PartidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPartidosScreen(
    navController: NavHostController,
    partidosViewModel: PartidosViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val partidos by partidosViewModel.listaPartidos.collectAsState(initial = emptyList())
    val usuarios by usuarioViewModel.listaUsuarios.collectAsState(initial = emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        partidosViewModel.cargarPartidos()
        usuarioViewModel.cargarTodosLosUsuarios()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Partidos", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        },
        bottomBar = { BottomNavigationBarAdminAGICP(navController) },
        containerColor = Color(0xFFFF9800)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(partidos) { partido ->
                    AdminPartidoCard(
                        partido = partido,
                        allUsers = usuarios,
                        onAdd = { user ->
                            partidosViewModel.unirseAPartido(partido.id, user.id)
                            Toast.makeText(context, "${user.nombre} añadido", Toast.LENGTH_SHORT).show()
                        },
                        onRemove = { userId ->
                            partidosViewModel.salirDePartido(partido.id, userId)
                            Toast.makeText(context, "Participante eliminado", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteMatch = {
                            partidosViewModel.eliminarPartido(partido.id)
                            Toast.makeText(context, "Partido eliminado", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun AdminPartidoCard(
    partido: Partido,
    allUsers: List<Usuario>,
    onAdd: (Usuario) -> Unit,
    onRemove: (String) -> Unit,
    onDeleteMatch: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val currentParticipants = remember(partido) {
        partido.participantes.keys
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fecha: ${partido.fechaHora}", style = MaterialTheme.typography.titleMedium)
                    Text("Pista: ${partido.pistaId}", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onDeleteMatch) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar partido")
                }
            }
            Spacer(Modifier.height(8.dp))

            // Lista de participantes
            Text("Participantes (${currentParticipants.size}/4):", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            currentParticipants.forEach { userId ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = userId, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemove(userId) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar participante")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            // Botones de Añadir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    enabled = currentParticipants.size < 4
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Añadir participante")
                }
            }
        }
    }

    // Diálogo para elegir usuario a añadir
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Selecciona un usuario") },
            text = {
                LazyColumn {
                    // Filtramos usuarios que aún no están en el partido
                    items(allUsers.filter { it.id !in currentParticipants }) { user ->
                        Text(
                            text = "${user.nombre} ${user.apellido}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAdd(user)
                                    showAddDialog = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

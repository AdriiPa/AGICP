package com.example.agicp.presentation.pistas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agicp.data.model.Pista
import com.example.agicp.data.model.Partido
import com.example.agicp.presentation.admin.BottomNavigationBarAdminAGICP
import com.example.agicp.viewmodel.partidosViewModel.PartidosViewModel
import com.example.agicp.viewmodel.pistasViewModel.PistasViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPistasScreen(
    navController: NavHostController,
    pistasViewModel: PistasViewModel,
    partidosViewModel: PartidosViewModel
) {
    val context = LocalContext.current
    val pistas by pistasViewModel.listaPistas.collectAsState()
    val partidos by partidosViewModel.listaPartidos.collectAsState()
    var showNewDialog by remember { mutableStateOf(false) }
    var showAssignDialogFor by remember { mutableStateOf<Pista?>(null) }

    LaunchedEffect(Unit) {
        pistasViewModel.cargarTodasLasPistas()
        partidosViewModel.cargarPartidos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Pistas", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewDialog = true }) {
                Text("+")
            }
        },
        bottomBar = { BottomNavigationBarAdminAGICP(navController) },
        containerColor = Color(0xFFFF9800)
    ) { p ->
        Box(
            Modifier
                .padding(p)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(pistas) { pista ->
                    PistaCard(
                        pista = pista,
                        partidos = partidos.filter { it.id in pistasViewModel.partidosAsignados.value },
                        onToggleAvailable = { pistasViewModel.cambiarDisponibilidad(pista.id, !pista.disponible) },
                        onDelete = {
                            pistasViewModel.eliminarPista(pista.id)
                            Toast.makeText(context, "Pista eliminada", Toast.LENGTH_SHORT).show()
                        },
                        onAssign = { showAssignDialogFor = pista }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            // New Pista Dialog
            if (showNewDialog) {
                var nombre by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showNewDialog = false },
                    title = { Text("Nueva Pista") },
                    text = {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre de la pista") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (nombre.isNotBlank()) {
                                val nueva = pistasViewModel.nuevaPistaTemplate(nombre)
                                pistasViewModel.crearPista(nueva)
                                Toast.makeText(context, "Pista creada", Toast.LENGTH_SHORT).show()
                                showNewDialog = false
                            }
                        }) { Text("Crear") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNewDialog = false }) { Text("Cancelar") }
                    }
                )
            }

            // Assign Partido Dialog
            showAssignDialogFor?.let { pista ->
                val assigned = remember { mutableStateListOf<String>().apply { addAll(pistasViewModel.partidosAsignados.value) } }
                AlertDialog(
                    onDismissRequest = { showAssignDialogFor = null },
                    title = { Text("Asignar partido a ${pista.nombre}") },
                    text = {
                        Column {
                            val availableToAssign = partidos
                                .filter { it.id !in assigned && assigned.size < 4 }
                            if (availableToAssign.isEmpty()) {
                                Text("No hay partidos disponibles o ya asignados o el máximo (4) fue alcanzado.")
                            } else {
                                LazyColumn {
                                    items(availableToAssign) { partido ->
                                        val formatted = partido.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm dd/MM"))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Text(formatted, Modifier.weight(1f))
                                            TextButton(onClick = {
                                                pistasViewModel.asignarPartido(pista.id, partido.id)
                                                Toast.makeText(context, "Partido asignado", Toast.LENGTH_SHORT).show()
                                                showAssignDialogFor = null
                                            }) {
                                                Text("Asignar")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAssignDialogFor = null }) { Text("Cerrar") }
                    },
                    dismissButton = {}
                )
            }
        }
    }
}

@Composable
private fun PistaCard(
    pista: Pista,
    partidos: List<Partido>,
    onToggleAvailable: () -> Unit,
    onDelete: () -> Unit,
    onAssign: () -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(pista.nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Switch(checked = pista.disponible, onCheckedChange = { onToggleAvailable() })
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Eliminar") }
            }
            Spacer(Modifier.height(8.dp))
            Text("Partidos asignados:", style = MaterialTheme.typography.bodyMedium)
            if (partidos.isEmpty()) {
                Text("—", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                partidos.forEach { partido ->
                    val t = partido.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm dd/MM"))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t, Modifier.weight(1f))
                        IconButton(onClick = {
                            // desasignar
                            onAssign() // should be replaced with desasign
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Desasignar")
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAssign, enabled = partidos.size < 4,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Asignar partido")
            }
        }
    }
}

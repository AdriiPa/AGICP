package com.example.agicp.presentation.torneos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.agicp.data.model.Torneo
import com.example.agicp.presentation.admin.BottomNavigationBarAdminAGICP
import com.example.agicp.viewmodel.TorneosViewModel
import com.example.agicp.viewmodel.pistasViewModel.PistasViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTorneosScreen(
    navController: NavHostController,
    torneosVM: TorneosViewModel,
    pistasVM: PistasViewModel
) {
    val context = LocalContext.current

    // Observamos la lista de torneos y la lista de usuarios desde el ViewModel
    val torneos by torneosVM.listaTorneos.collectAsState()
    val allUsers by torneosVM.listaUsuarios.collectAsState()
    val pistas by pistasVM.listaPistas.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingTorneo by remember { mutableStateOf<Torneo?>(null) }
    var managingTorneo by remember { mutableStateOf<Torneo?>(null) }

    // Al arrancar la pantalla cargamos torneos, usuarios y pistas
    LaunchedEffect(Unit) {
        torneosVM.cargarTorneos()
        torneosVM.cargarUsuarios()
        pistasVM.cargarTodasLasPistas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Torneos", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Torneo")
            }
        },
        bottomBar = { BottomNavigationBarAdminAGICP(navController) },
        containerColor = Color(0xFFFF9800)
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(torneos) { torneo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(torneo.nombre, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            Text(
                                "Fechas: ${torneo.fechaInicio.format(fmt)} – ${
                                    torneo.fechaFin.format(
                                        fmt
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Máx. participantes: ${torneo.maxParticipantes}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(8.dp))

                            Text(
                                "Inscritos (${torneo.participantes.size}):",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            torneo.participantes.forEach { uid ->
                                val user = allUsers.find { it.id == uid.toString() }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(user?.nombreUsuario ?: uid.toString(), Modifier.weight(1f))
                                    IconButton(onClick = {
                                        torneosVM.anularInscripcion(torneo.id, uid.toString())
                                        Toast.makeText(
                                            context,
                                            "Usuario dado de baja",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Quitar")
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = { managingTorneo = torneo }) {
                                    Text("Gestionar Inscripciones")
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = { editingTorneo = torneo }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = {
                                    torneosVM.eliminarTorneo(torneo.id)
                                    Toast.makeText(context, "Torneo eliminado", Toast.LENGTH_SHORT)
                                        .show()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo para Crear / Editar torneo
        if (showCreateDialog || editingTorneo != null) {
            var nombre by remember { mutableStateOf(editingTorneo?.nombre.orEmpty()) }
            var fechaInicio by remember { mutableStateOf(editingTorneo?.fechaInicio?.format(DateTimeFormatter.ISO_DATE) ?: "") }
            var fechaFin by remember { mutableStateOf(editingTorneo?.fechaFin?.format(DateTimeFormatter.ISO_DATE) ?: "") }
            var maxPart by remember { mutableStateOf(editingTorneo?.maxParticipantes?.toString().orEmpty()) }

            AlertDialog(
                onDismissRequest = {
                    showCreateDialog = false
                    editingTorneo = null
                },
                title = { Text(if (editingTorneo == null) "Crear Torneo" else "Editar Torneo") },
                text = {
                    Column {
                        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fechaInicio,
                            onValueChange = { fechaInicio = it },
                            label = { Text("Fecha Inicio (YYYY-MM-DD)") }
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fechaFin,
                            onValueChange = { fechaFin = it },
                            label = { Text("Fecha Fin (YYYY-MM-DD)") }
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = maxPart,
                            onValueChange = { maxPart = it.filter(Char::isDigit) },
                            label = { Text("Máx. Participantes") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val t = Torneo(
                            id = editingTorneo?.id ?: torneosVM.nuevoId(),
                            nombre = nombre,
                            fechaInicio = java.time.LocalDate.parse(fechaInicio).toString(),
                            fechaFin = java.time.LocalDate.parse(fechaFin).toString(),
                            maxParticipantes = maxPart.toIntOrNull() ?: 0,
                            participantes = editingTorneo?.participantes ?: emptyMap()
                        )
                        if (editingTorneo == null) torneosVM.crearTorneo(t)
                        else torneosVM.actualizarTorneo(t)
                        Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show()
                        showCreateDialog = false
                        editingTorneo = null
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showCreateDialog = false
                        editingTorneo = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo para Gestionar Inscripciones
        managingTorneo?.let { torneo ->
            AlertDialog(
                onDismissRequest = { managingTorneo = null },
                title = { Text("Inscribir jugadores en \"${torneo.nombre}\"") },
                text = {
                    val inscritos = torneo.participantes
                    val disponibles = allUsers.filter { it.id !in inscritos }
                    Column {
                        if (inscritos.size < torneo.maxParticipantes) {
                            Text("Usuarios disponibles:")
                            LazyColumn {
                                items(disponibles) { user ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(user.nombreUsuario, Modifier.weight(1f))
                                        TextButton(onClick = {
                                            torneosVM.inscribirJugador(torneo.id, user.id)
                                            Toast.makeText(context, "Usuario inscrito", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Text("Inscribir")
                                        }
                                    }
                                }
                            }
                        } else {
                            Text("Máximo de participantes alcanzado")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { managingTorneo = null }) { Text("Cerrar") }
                },
                dismissButton = {}
            )
        }
    }
}

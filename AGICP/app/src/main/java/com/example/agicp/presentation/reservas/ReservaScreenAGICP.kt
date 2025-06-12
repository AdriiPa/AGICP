package com.example.agicp.presentation.reservas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agicp.core.navigation.Rutas
import com.example.agicp.presentation.dashboard.BottomNavigationBarAGICP
import com.example.agicp.viewmodel.UsuarioViewModel
import com.example.agicp.viewmodel.partidosViewModel.PartidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreenAGICP(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    partidosViewModel: PartidosViewModel
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val partidos by partidosViewModel.listaPartidos.collectAsState()
    val userId = usuario?.id ?: ""

    // Filtramos partidos donde participa este usuario
    val reservasUsuario = partidos.filter { it.participantes.containsKey(userId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Rutas.RESERVAR) }, // Ruta para nueva reserva
                containerColor = Color(0xFF003366)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva", tint = Color.White)
            }
        },
        bottomBar = { BottomNavigationBarAGICP(navController) },
        containerColor = Color(0xFFFF9800)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFF9800))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (reservasUsuario.isEmpty()) {
                Text("No tienes reservas.", color = Color.White)
            } else {
                LazyColumn {
                    items(reservasUsuario) { partido ->
                        ReservaCardAGICP(
                            partido = partido,
                            userId = userId,
                            onCancelar = {
                                partidosViewModel.salirDePartido(partido.id, userId)
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCardAGICP(
    partido: com.example.agicp.data.model.Partido,
    userId: String,
    onCancelar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Fecha: ${partido.fechaHora}", style = MaterialTheme.typography.bodyLarge)
            Text("Pista: ${partido.pistaId}", style = MaterialTheme.typography.bodyMedium)
            Text("ID Partido: ${partido.id}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(
                "Participantes: ${partido.participantes.keys.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall
            )
            partido.resultado?.let {
                Text("Resultado: $it", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onCancelar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar")
                Spacer(Modifier.width(4.dp))
                Text("Cancelar Reserva")
            }
        }
    }
}


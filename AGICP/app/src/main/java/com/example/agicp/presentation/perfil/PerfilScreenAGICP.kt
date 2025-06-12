package com.example.agicp.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.agicp.core.navigation.Rutas
import com.example.agicp.data.model.Usuario
import com.example.agicp.presentation.dashboard.BottomNavigationBarAGICP
import com.example.agicp.viewmodel.AuthViewModel
import com.example.agicp.viewmodel.UsuarioViewModel
import com.example.agicp.viewmodel.partidosViewModel.PartidosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreenAGICP(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    partidosViewModel: PartidosViewModel,
    authViewModel: AuthViewModel
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val nombre = usuario?.nombre ?: "Jugador"
    val apellido = usuario?.apellido ?: ""
    val nivel = usuario?.nivel ?: 1
    val atributos = usuario?.atributosJugador ?: "No definidos"
    val userId = usuario?.id.orEmpty()

    // Estadísticas
    val (jugados, ganados, perdidos) = remember(userId, partidosViewModel.listaPartidos.value) {
        if (userId.isNotEmpty()) {
            partidosViewModel.getEstadisticasUsuario(userId)
        } else Triple(0, 0, 0)
    }

    val partidosIds = usuario?.partidosParticipados ?: emptyList()
    val partidosAll by partidosViewModel.listaPartidos.collectAsState()
    val partidosUsuario = partidosAll.filter { it.id in partidosIds }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        },
        bottomBar = { BottomNavigationBarAGICP(navController) },
        containerColor = Color(0xFFFF9800)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFF9800))
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003366)),
                contentAlignment = Alignment.Center
            ) {
                val iniciales = buildString {
                    if (nombre.isNotEmpty()) append(nombre.first())
                    if (apellido.isNotEmpty()) append(apellido.first())
                }.uppercase()
                Text(
                    text = iniciales.ifEmpty { "U" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "$nombre $apellido",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366),
                fontSize = 24.sp
            )

            Spacer(Modifier.height(12.dp))

            // Nivel y atributos
            Text(
                "Nivel: $nivel",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text("Atributos: $atributos", fontSize = 16.sp, color = Color.DarkGray)
            Spacer(Modifier.height(20.dp))

            // Resultados (ganados/perdidos) - Si tienes esos datos, si no, omite este bloque
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                ResultBox("Jugados", jugados, Color(0xFF1976D2))
                ResultBox("Ganados", ganados, Color(0xFF43A047))
                ResultBox("Perdidos", perdidos, Color(0xFFD32F2F))
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Partidos Participados:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF003366)
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                if (partidosUsuario.isEmpty()) {
                    item {
                        Text(
                            text = "No ha participado en ningún partido todavía.",
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }
                } else {
                    items(partidosUsuario) { partido ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = "Partido: ${partido.id}",
                                    color = Color(0xFF003366),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Resultado: ${partido.resultado}",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

    @Composable
    fun ResultBox(titulo: String, cantidad: Int, color: Color) {
        Card(
            modifier = Modifier
                .width(100.dp)
                .height(70.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "$cantidad",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
    }

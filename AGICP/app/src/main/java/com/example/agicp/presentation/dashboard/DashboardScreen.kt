package com.example.agicp.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.agicp.R
import com.example.agicp.core.navigation.Rutas
import com.example.agicp.data.model.Usuario
import com.example.agicp.viewmodel.AuthViewModel
import com.example.agicp.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    authViewModel: AuthViewModel
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    var menuAbierto by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_agicp),
                            contentDescription = "Logo AGICP",
                            modifier = Modifier.size(40.dp).padding(end = 8.dp)
                        )
                        Text(
                            text = "AGICP Club",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión") },
                                onClick = {
                                    menuAbierto = false
                                    authViewModel.signOut(context, navController, usuarioViewModel)
                                }
                            )
                        }
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PerfilSectionAGICP(usuario, navController)
            Spacer(Modifier.height(12.dp))
            NavigationButtonsAGICP(navController)
        }
    }
}

@Composable
fun PerfilSectionAGICP(usuario: Usuario?, navController: NavHostController) {
    val nombre = usuario?.nombre?.ifEmpty { "Jugador" } ?: "Jugador"
    val apellido = usuario?.apellido ?: ""
    // Puedes usar las iniciales como avatar
    val iniciales = buildString {
        if (nombre.isNotEmpty()) append(nombre.first())
        if (apellido.isNotEmpty()) append(apellido.first())
    }.uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(18.dp)),
        onClick = { navController.navigate(Rutas.PERFIL) },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Círculo con iniciales como avatar
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003366)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales.ifEmpty { "U" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Hola, $nombre 👋",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003366),
                    fontSize = 20.sp
                )
                Text(
                    text = "¡Bienvenido a tu club de pádel!",
                    color = Color(0xFF555555)
                )
            }
        }
    }
}


@Composable
fun NavigationButtonsAGICP(navController: NavHostController) {
    val items = listOf(
        NavItem("Perfil", Rutas.PERFIL, Icons.Default.AccountCircle),
        NavItem("Reservas", Rutas.PISTAS, Icons.Default.Notifications),
        NavItem("Torneos", Rutas.TORNEOS, Icons.Default.Place),
        NavItem("Amigos", Rutas.AMIGOS, Icons.Default.Face)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(items) { item ->
            Button(
                onClick = { navController.navigate(item.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF003366),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(item.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BottomNavigationBarAGICP(navController: NavHostController) {
    val items = listOf(
        NavItem("Perfil", Rutas.PERFIL, Icons.Default.AccountCircle),
        NavItem("Inicio", Rutas.DASHBOARD, Icons.Default.Home),
        NavItem("Reservas", Rutas.PISTAS, Icons.Default.Notifications),
        NavItem("Torneos", Rutas.TORNEOS, Icons.Default.Place),
        NavItem("Amigos", Rutas.AMIGOS, Icons.Default.Face)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color(0xFF003366)) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title, tint = Color.White) },
                label = { Text(item.title, color = Color.White) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

data class NavItem(val title: String, val route: String, val icon: ImageVector)

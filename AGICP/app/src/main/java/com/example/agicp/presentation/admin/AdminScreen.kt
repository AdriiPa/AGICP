package com.example.agicp.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.agicp.core.navigation.Rutas
import com.example.agicp.viewmodel.AuthViewModel
import com.example.agicp.viewmodel.UsuarioViewModel

data class NavItem(val title: String, val route: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel de Administración",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Menú de usuario",
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
                                    authViewModel.signOut(
                                        context = context,
                                        navController = navController,
                                        usuarioViewModel = usuarioViewModel
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF003366))
            )
        },
        containerColor = Color(0xFFFF9800),
        bottomBar = { BottomNavigationBarAdminAGICP(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AdminNavigationGridAGICP(navController)
        }
    }
}

@Composable
fun AdminNavigationGridAGICP(navController: NavHostController) {
    val items = listOf(
        NavItem("Usuarios", Rutas.ADMIN_USUARIOS, Icons.Default.AccountCircle),
        NavItem("Pistas", Rutas.ADMIN_PISTAS, Icons.Default.Home),
        NavItem("Torneos", Rutas.ADMIN_TORNEOS, Icons.Default.AccountBox),
        NavItem("Partidos", Rutas.ADMIN_PARTIDOS, Icons.Default.DateRange)
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                Button(
                    onClick = { navController.navigate(item.route) },
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff39e600))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = item.icon, contentDescription = item.title, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(item.title, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBarAdminAGICP(navController: NavHostController) {
    val items = listOf(
        NavItem("Usuarios", Rutas.ADMIN_USUARIOS, Icons.Default.AccountCircle),
        NavItem("Pistas", Rutas.ADMIN_PISTAS, Icons.Default.Home),
        NavItem("Torneos", Rutas.ADMIN_TORNEOS, Icons.Default.AccountBox),
        NavItem("Partidos", Rutas.ADMIN_PARTIDOS, Icons.Default.DateRange)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color(0xFF003366)) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title, tint = Color.White) },
                label = { Text(item.title, color = Color.White, fontWeight = FontWeight.Bold) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Rutas.ADMIN_USUARIOS) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

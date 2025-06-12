package com.example.agicp.presentation.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agicp.core.navigation.Rutas

data class NavItem(val title: String, val route: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel de Administración") })
        },
        bottomBar = {
            AdminBottomBar(
                selectedIndex = selectedTab,
                onSelect = { idx ->
                    selectedTab = idx
                    navController.navigate(
                        when(idx) {
                            0 -> Rutas.USUARIOS
                            1 -> Rutas.PISTAS
                            else -> Rutas.TORNEOS
                        }
                    ) {
                        // evita múltiples instancias
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { inner ->
        Box(Modifier.padding(inner)) {
            // aquí podría ir tu contenido si quisieras anidar Composables
        }
    }
}

@Composable
fun AdminBottomBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val items = listOf(
        NavItem("Usuarios", Rutas.USUARIOS, Icons.Default.AccountCircle),
        NavItem("Pistas",   Rutas.PISTAS,   Icons.Default.Home),
        NavItem("Torneos",  Rutas.TORNEOS,  Icons.Default.AccountBox)
    )

    NavigationBar(containerColor = Color(0xFF003366)) {
        items.forEachIndexed { idx, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.title) },
                selected = idx == selectedIndex,
                onClick = { onSelect(idx) },
                alwaysShowLabel = true
            )
        }
    }
}

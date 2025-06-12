package com.example.agicp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agicp.presentation.login.LoginScreen
import com.example.agicp.presentation.registro.RegistroScreen
import com.example.agicp.viewmodel.AuthViewModel
import com.example.agicp.viewmodel.UsuarioViewModel
import com.example.agicp.core.navigation.Rutas
import com.example.agicp.core.theme.AGICPTheme
import com.example.agicp.presentation.admin.AdminScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // Asegura que la app ocupe toda la pantalla

        setContent {
            AGICPTheme {
                // Inicializamos NavController para la navegación
                val navController = rememberNavController()

                // Usamos `viewModel()` para obtener los ViewModels
                val authViewModel: AuthViewModel = viewModel()
                val usuarioViewModel: UsuarioViewModel = viewModel()

                // Llamamos a la función que contiene las rutas
                Navegacion(navController, authViewModel, usuarioViewModel)
            }
        }
    }
}

@Composable
fun Navegacion(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    // Aquí definimos las rutas principales de la app
    NavHost(navController = navController, startDestination = Rutas.LOGIN) {
        // Pantalla de Login
        composable(Rutas.LOGIN) {
            LoginScreen(navController, authViewModel, usuarioViewModel)
        }

        // Pantalla de Registro
        composable(Rutas.REGISTRO) {
            RegistroScreen(navController, authViewModel)
        }

        // Pantalla Dashboard (después de registro/login exitoso)
        composable(Rutas.DASHBOARD) {
            //DashboardScreen(navController, usuarioViewModel)
        }

        // Pantalla Admin (solo accesible para admins)
        composable(Rutas.ADMIN) {
            AdminScreen(navController, usuarioViewModel, authViewModel,navController)
        }
    }
}

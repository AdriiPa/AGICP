package com.example.agicp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agicp.core.navigation.Rutas
import com.example.agicp.core.theme.AGICPTheme
import com.example.agicp.presentation.admin.AdminPartidosScreen
import com.example.agicp.presentation.admin.AdminScreen
import com.example.agicp.presentation.dashboard.DashboardScreen
import com.example.agicp.presentation.login.LoginScreen
import com.example.agicp.presentation.perfil.PerfilScreenAGICP
import com.example.agicp.presentation.pistas.AdminPistasScreen
import com.example.agicp.presentation.registro.RegistroScreen
import com.example.agicp.presentation.reservas.ReservaPistaScreenAGICP
import com.example.agicp.presentation.reservas.ReservasScreenAGICP
import com.example.agicp.presentation.torneos.AdminTorneosScreen
import com.example.agicp.presentation.usuarios.VerUsuariosScreen
import com.example.agicp.viewmodel.*
import com.example.agicp.viewmodel.partidosViewModel.PartidosViewModel
import com.example.agicp.viewmodel.pistasViewModel.PistasViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            AGICPTheme {
                val navController = rememberNavController()

                val authViewModel: AuthViewModel = viewModel()
                val usuarioViewModel: UsuarioViewModel = viewModel()
                val pistasViewModel: PistasViewModel = viewModel()
                val torneosViewModel: TorneosViewModel = viewModel()
                val partidosViewModel: PartidosViewModel = viewModel()

                Navegacion(
                    navController,
                    authViewModel,
                    usuarioViewModel,
                    partidosViewModel,
                    pistasViewModel,
                    torneosViewModel
                )


            }
        }
    }
}

@Composable
fun Navegacion(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    usuarioViewModel: UsuarioViewModel,
    partidosViewModel: PartidosViewModel,
    pistasViewModel: PistasViewModel,
    torneosViewModel: TorneosViewModel
) {
    NavHost(navController = navController, startDestination = Rutas.LOGIN) {

        composable(Rutas.LOGIN) {
            LoginScreen(navController, authViewModel, usuarioViewModel)
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(navController, authViewModel)
        }

        composable(Rutas.DASHBOARD) {
            DashboardScreen(navController,usuarioViewModel,authViewModel)
        }
        composable(Rutas.PERFIL){
            PerfilScreenAGICP(navController,usuarioViewModel,partidosViewModel,authViewModel)
        }

        composable(Rutas.ADMIN_USUARIOS) {
            VerUsuariosScreen(navController, usuarioViewModel, authViewModel)
        }
        composable(Rutas.ADMIN_PISTAS) {
            AdminPistasScreen(navController, pistasViewModel, partidosViewModel)
        }
        composable(Rutas.ADMIN_TORNEOS) {
            AdminTorneosScreen(navController, torneosViewModel, pistasViewModel)
        }
        composable(Rutas.ADMIN_PARTIDOS) {
            AdminPartidosScreen(navController, partidosViewModel, usuarioViewModel)
        }
        composable(Rutas.ADMIN) {
            AdminScreen(navController, usuarioViewModel, authViewModel)
        }
        composable(Rutas.PISTAS) {
            ReservasScreenAGICP(navController, usuarioViewModel, partidosViewModel)
        }
        composable(Rutas.RESERVAR) {
            ReservaPistaScreenAGICP(navController, usuarioViewModel, pistasViewModel, partidosViewModel)
        }


    }
}


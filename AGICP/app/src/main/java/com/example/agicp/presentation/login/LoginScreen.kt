package com.example.agicp.presentation.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agicp.R
import com.example.agicp.viewmodel.AuthViewModel
import com.example.agicp.viewmodel.UsuarioViewModel
import com.example.agicp.core.navigation.Rutas
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val fondoLogin =
        painterResource(id = R.drawable.fondo_login) // Asegúrate de poner la imagen en drawable
    // Colores AGICP
    val verdePadel = colorResource(id = R.color.verde_padel)
    val azulDeporte = Color(0xFF003366)
    val grisClaro = colorResource(id = R.color.gris_claro)
    val naranjaVivo = colorResource(id = R.color.naranja_vivo)

    // Estado UI
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val loginSuccess by authViewModel.loginSuccess.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val userNotActivated by authViewModel.userNotActivated.collectAsState()

    // --------- GOOGLE SIGN IN LAUNCHER ----------
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.result
                val idToken = account?.idToken
                if (idToken != null) {
                    authViewModel.loginWithGoogle(idToken, navController, usuarioViewModel)
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Error con Google Sign-In", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchGoogleSignIn() {
        val googleSignInClient = GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.google_app_id)) // TU CLIENT_ID EN STRINGS
                .requestEmail()
                .build()
        )
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    // Alerta si el usuario no está activado
    if (userNotActivated) {
        AlertDialog(
            onDismissRequest = { authViewModel.userNotActivated.value = false },
            confirmButton = {
                Button(onClick = { authViewModel.userNotActivated.value = false }) {
                    Text("OK")
                }
            },
            title = { Text("Cuenta no activada", color = naranjaVivo) },
            text = { Text("Debes esperar a que un administrador active tu cuenta antes de poder acceder.") }
        )
    }

    // Redirección tras login
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            val userRole = authViewModel.userRole.value
            if (userRole == "admin") {
                navController.navigate(Rutas.ADMIN) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {
                navController.navigate(Rutas.DASHBOARD) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(azulDeporte), // Fondo azul oscuro
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = fondoLogin,
            contentDescription = "Fondo de login",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .background(color = naranjaVivo, RoundedCornerShape(24.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.logo_agicp),
                contentDescription = "Logo AGICP",
                tint = verdePadel,
                modifier = Modifier.size(96.dp)
            )

            Spacer(Modifier.height(16.dp))
            Text(
                "Bienvenido a AGICP",
                color = Color.White, // Texto blanco
                fontSize = 30.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold

            )
            Spacer(Modifier.height(32.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = azulDeporte) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Botón login email
            Button(
                onClick = {
                    authViewModel.loginWithEmail(
                        email.trim(),
                        password.trim(),
                        navController,
                        usuarioViewModel
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdePadel)
            ) {
                Text("Iniciar sesión", color = Color.White, fontSize = 18.sp)
            }

            Spacer(Modifier.height(12.dp))

            // Botón login Google - redondo con logo
            Button(
                onClick = { launchGoogleSignIn() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(8.dp),  // Añadimos un poco de padding
                shape = RoundedCornerShape(50), // Botón redondeado
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_google),
                    contentDescription = "Google Sign-In",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))  // Espacio entre el logo y el texto
                Text("Continuar con Google", color = Color.Black, fontSize = 18.sp)
            }
            Spacer(Modifier.height(20.dp))

            // Error
            if (!errorMessage.isNullOrEmpty()) {
                Text(errorMessage ?: "", color = Color.Red, fontSize = 15.sp)
                Spacer(Modifier.height(8.dp))
            }

            // Loading
            if (isLoading) {
                CircularProgressIndicator(color = azulDeporte)
                Spacer(Modifier.height(12.dp))
            }

            // Ir a registro
            TextButton(
                onClick = { navController.navigate(Rutas.REGISTRO) }
            ) {
                Text("¿No tienes cuenta? Regístrate aquí", color = azulDeporte)
            }
        }
    }
}


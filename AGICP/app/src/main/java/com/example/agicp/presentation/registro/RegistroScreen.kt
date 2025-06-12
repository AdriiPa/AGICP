package com.example.agicp.presentation.registro

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agicp.R
import com.example.agicp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current

    // Colores AGICP desde resources
    val verdePadel = colorResource(id = R.color.verde_padel)
    val azulDeporte = colorResource(id = R.color.azul_deporte)
    val grisClaro = colorResource(id = R.color.gris_claro)
    val naranjaVivo = colorResource(id = R.color.naranja_vivo)

    // Estado campos
    var nombreUsuario by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var edadText by remember { mutableStateOf("") }
    var nivel by remember { mutableStateOf(1) }

    // Atributos del jugador
    val manos = listOf("Derecha", "Zurda")
    val posiciones = listOf("Derecha", "Revés")
    var manoSeleccionada by remember { mutableStateOf(manos[0]) }
    var posicionSeleccionada by remember { mutableStateOf(posiciones[0]) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val userNotActivated by authViewModel.userNotActivated.collectAsState()

    fun registroValido(): Boolean =
        nombreUsuario.isNotBlank() && nombre.isNotBlank() && apellido.isNotBlank()
                && email.isNotBlank() && password.length >= 6 && edadText.toIntOrNull() != null

    Box(
        Modifier
            .fillMaxSize()
            .background(azulDeporte),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(color = naranjaVivo, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Registro de Jugador", color = azulDeporte, fontSize = 30.sp, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text("Nombre de usuario") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (mínimo 6)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = edadText,
                onValueChange = { edadText = it.filter { c -> c.isDigit() } },
                label = { Text("Edad") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(10.dp))

            // Slider de nivel (1-7)
            Text("Nivel de juego: $nivel", modifier = Modifier.align(Alignment.Start), color = verdePadel, fontWeight = FontWeight.Bold)
            Slider(
                value = nivel.toFloat(),
                onValueChange = { nivel = it.toInt() },
                valueRange = 1f..7f,
                steps = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // Dropdown mano preferida
            var manoDropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = manoDropdownExpanded,
                onExpandedChange = { manoDropdownExpanded = !manoDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = manoSeleccionada,
                    onValueChange = {},
                    label = { Text("Mano preferida") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = manoDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = manoDropdownExpanded,
                    onDismissRequest = { manoDropdownExpanded = false }
                ) {
                    manos.forEach { mano ->
                        DropdownMenuItem(
                            text = { Text(mano) },
                            onClick = {
                                manoSeleccionada = mano
                                manoDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Dropdown posición en pista
            var posDropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = posDropdownExpanded,
                onExpandedChange = { posDropdownExpanded = !posDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = posicionSeleccionada,
                    onValueChange = {},
                    label = { Text("Posición en pista") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(
                    expanded = posDropdownExpanded,
                    onDismissRequest = { posDropdownExpanded = false }
                ) {
                    posiciones.forEach { pos ->
                        DropdownMenuItem(
                            text = { Text(pos) },
                            onClick = {
                                posicionSeleccionada = pos
                                posDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Botón de registro
            Button(
                onClick = {
                    if (!registroValido()) {
                        Toast.makeText(context, "Por favor, rellena todos los campos correctamente.", Toast.LENGTH_SHORT).show()
                    } else {
                        val atributosJugador = "${manoSeleccionada} - ${posicionSeleccionada}"
                        authViewModel.registerWithEmail(
                            email.trim(),
                            password.trim(),
                            nombre.trim(),
                            apellido.trim(),
                            nombreUsuario.trim(),
                            edadText.toInt(),
                            nivel,
                            atributosJugador.trim(),
                            navController
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdePadel)
            ) {
                Text("Registrarse", color = Color.White, fontSize = 18.sp)
            }

            if (isLoading) {
                Spacer(Modifier.height(12.dp))
                CircularProgressIndicator(color = azulDeporte)
            }

            if (!errorMessage.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage ?: "", color = Color.Red, fontSize = 15.sp)
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = azulDeporte)
            }
        }
    }
}

package com.example.agicp.presentation.reservas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange

import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agicp.data.model.Partido
import com.example.agicp.data.model.Pista
import com.example.agicp.viewmodel.UsuarioViewModel
import com.example.agicp.viewmodel.partidosViewModel.PartidosViewModel
import com.example.agicp.viewmodel.pistasViewModel.PistasViewModel
import com.example.agicp.core.navigation.Rutas
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaPistaScreenAGICP(
    navController: NavHostController,
    usuarioViewModel: UsuarioViewModel,
    pistasViewModel: PistasViewModel,
    partidosViewModel: PartidosViewModel
) {
    val context = LocalContext.current
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val pistas by pistasViewModel.listaPistas.collectAsState()
    val usuarios by usuarioViewModel.listaUsuarios.collectAsState(initial = emptyList())

    // Pistas disponibles
    val pistasDisponibles = pistas.filter { it.disponible }

    var pistaSeleccionada by remember { mutableStateOf<Pista?>(null) }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()

    // Selección de amigos (solo los de la lista de amigos)
    val amigosDisponibles = usuarios.filter { it.id in (usuario?.amigos ?: emptyList()) }
    val (amigosSeleccionados, setAmigosSeleccionados) = remember { mutableStateOf<Set<String>>(emptySet()) }
    var showAmigosDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservar Pista", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF003366))
            )
        },
        bottomBar = { /* Aquí tu BottomNavigationBarAGICP(navController) si quieres */ },
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
            Text("Selecciona una pista:", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.weight(1f, false).heightIn(max = 200.dp)) {
                items(pistasDisponibles) { pista ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { pistaSeleccionada = pista },
                        colors = CardDefaults.cardColors(
                            containerColor = if (pista == pistaSeleccionada) Color(0xFF003366) else Color.White
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pista",
                                tint = if (pista == pistaSeleccionada) Color.White else Color(0xFF003366)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                pista.nombre,
                                color = if (pista == pistaSeleccionada) Color.White else Color(0xFF003366),
                                fontWeight = FontWeight.Bold
                            )
                            if (pista == pistaSeleccionada) {
                                Icon(Icons.Default.Check, contentDescription = "Seleccionada", tint = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Selector de fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                label = { Text("Fecha (dd/MM/yyyy)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                readOnly = true,
                enabled = true
            )
            Spacer(Modifier.height(8.dp))

            // Selector de hora
            OutlinedTextField(
                value = hora,
                onValueChange = {},
                label = { Text("Hora (HH:mm)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                readOnly = true,
                enabled = true
            )
            Spacer(Modifier.height(10.dp))

            // Selector de amigos
            Button(
                onClick = { showAmigosDialog = true },
                enabled = amigosDisponibles.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir amigos")
                Spacer(Modifier.width(8.dp))
                Text(
                    if (amigosSeleccionados.isEmpty()) "Añadir amigos" else "${amigosSeleccionados.size} amigos añadidos"
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = {
                    if (usuario == null || pistaSeleccionada == null || fecha.isBlank() || hora.isBlank()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val fechaHora = "$fecha $hora"
                    // Crea participantes: el usuario y los amigos seleccionados
                    val participantes = mutableMapOf(usuario!!.id to true)
                    amigosSeleccionados.forEach { participantes[it] = true }
                    val partido = Partido(
                        id = UUID.randomUUID().toString(),
                        fechaHora = fechaHora,
                        pistaId = pistaSeleccionada!!.id,
                        participantes = participantes,
                        resultado = null
                    )
                    partidosViewModel.crearPartido(partido)
                    Toast.makeText(context, "Reserva creada correctamente", Toast.LENGTH_SHORT).show()
                    navController.navigate(Rutas.DASHBOARD) {
                        popUpTo(Rutas.DASHBOARD) { inclusive = true }
                        launchSingleTop = true
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = pistaSeleccionada != null && fecha.isNotBlank() && hora.isNotBlank()
            ) {
                Text("Reservar Pista")
            }
        }
    }

    // DatePickerDialog
    if (showDatePicker) {
        val hoy = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val date = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                fecha = date
                showDatePicker = false
            },
            hoy.get(Calendar.YEAR),
            hoy.get(Calendar.MONTH),
            hoy.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = hoy.timeInMillis // No dejar reservar en el pasado
            setOnDismissListener { showDatePicker = false }
        }.show()
    }

    // TimePickerDialog
    if (showTimePicker) {
        val ahora = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val horaTxt = "%02d:%02d".format(hourOfDay, minute)
                hora = horaTxt
                showTimePicker = false
            },
            ahora.get(Calendar.HOUR_OF_DAY),
            ahora.get(Calendar.MINUTE),
            true
        ).apply {
            setOnDismissListener { showTimePicker = false }
        }.show()
    }

    // Dialogo de selección de amigos
    if (showAmigosDialog) {
        AlertDialog(
            onDismissRequest = { showAmigosDialog = false },
            title = { Text("Añade amigos a la reserva") },
            text = {
                Column {
                    if (amigosDisponibles.isEmpty()) {
                        Text("No tienes amigos para añadir.")
                    } else {
                        amigosDisponibles.forEach { amigo ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val actual = amigosSeleccionados.toMutableSet()
                                        if (amigo.id in amigosSeleccionados) actual.remove(amigo.id)
                                        else actual.add(amigo.id)
                                        setAmigosSeleccionados(actual)
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = amigo.nombre,
                                    tint = if (amigo.id in amigosSeleccionados) Color(0xFF003366) else Color.Gray
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(amigo.nombre, color = Color.Black)
                                Spacer(Modifier.weight(1f))
                                if (amigo.id in amigosSeleccionados) {
                                    Icon(Icons.Default.Check, contentDescription = "Seleccionado", tint = Color(0xFF003366))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAmigosDialog = false }) { Text("Hecho") }
            }
        )
    }
}

package com.upao.HomeSecurity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.upao.HomeSecurity.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var alarmRef: DatabaseReference
    private lateinit var alarmSensorRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase Auth y Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Referencias a la alarma en Firebase
        alarmRef = database.getReference("alarmaActiva")
        alarmSensorRef = database.getReference("alarma/sensor")

        // Configuración de View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de ViewPager y TabLayout
        configurarViewPagerYTabs()

        // Botón de cerrar sesión
        binding.logoutButton.setOnClickListener {
            cerrarSesion()
        }
    }

    override fun onStart() {
        super.onStart()

        // Verifica si el usuario está autenticado
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirigirAlLogin()
        } else {
            Log.d("MainActivity", "Usuario autenticado: ${currentUser.email}")
            escucharEstadoAlarma()
            cargarDatos()
        }
    }

    private fun configurarViewPagerYTabs() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    private fun cargarDatos() {
        // Aquí puedes agregar la lógica para cargar los datos iniciales
        // Ejemplo: cargar estado de luces o el garage si es necesario
    }

    private fun escucharEstadoAlarma() {
        alarmRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isAlarmActive = snapshot.getValue(String::class.java)?.toBoolean() ?: false
                if (isAlarmActive) {
                    // Si la alarma está activa, muestra el diálogo para desactivar
                    alarmSensorRef.get().addOnSuccessListener { sensorSnapshot ->
                        val sensor = sensorSnapshot.getValue(String::class.java) ?: "desconocido"
                        mostrarDialogoAlarma(sensor)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al leer la alarma: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarDialogoAlarma(sensor: String) {
        val mensaje = when (sensor) {
            "puerta" -> "El sensor de puerta detectó una intrusión."
            "movimiento" -> "El sensor de movimiento detectó actividad sospechosa."
            else -> "La alarma fue activada por un sensor desconocido."
        }

        val inputField = android.widget.EditText(this)
        inputField.hint = "Ingresa el código de seguridad"

        AlertDialog.Builder(this)
            .setTitle("¡Alarma Activada!")
            .setMessage(mensaje)
            .setView(inputField)
            .setPositiveButton("Desactivar") { dialog, _ ->
                val codigoIngresado = inputField.text.toString()
                verificarCodigoSeguridad(codigoIngresado, dialog)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()

        activarVibracion()
    }

    private fun verificarCodigoSeguridad(codigo: String, dialog: DialogInterface) {
        val codigoRef = database.getReference("users/defaultUser/codigo")
        val modoSeguroRef = database.getReference("modoSeguro")

        // Verifica el código de seguridad
        codigoRef.get().addOnSuccessListener { snapshot ->
            val codigoCorrecto = snapshot.getValue()?.toString()
            if (codigoCorrecto == codigo) {
                // Desactiva el modo seguro en Firebase
                modoSeguroRef.setValue("off").addOnSuccessListener {
                    Toast.makeText(this, "Modo seguro desactivado", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Cierra el diálogo
                }.addOnFailureListener { error ->
                    Toast.makeText(this, "Error al desactivar el modo seguro: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { error ->
            Toast.makeText(this, "Error al verificar el código: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun activarVibracion() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(1000) // Vibrar durante 1 segundo
        }
    }

    fun registrarHistorial(accion: String) {
        val historialRef = database.reference.child("historial")

        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(timestamp)

        val nuevoHistorial = mapOf(
            "accion" to accion,
            "usuario" to (FirebaseAuth.getInstance().currentUser?.email ?: "Desconocido"),
            "timestamp" to formattedDate
        )

        historialRef.push().setValue(nuevoHistorial)
            .addOnSuccessListener {
                Toast.makeText(this, "Historial registrado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error al registrar historial: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun cerrarSesion() {
        auth.signOut()
        redirigirAlLogin()
    }

    private fun redirigirAlLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

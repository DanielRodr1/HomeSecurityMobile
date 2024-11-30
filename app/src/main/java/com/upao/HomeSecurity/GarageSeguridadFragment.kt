package com.upao.HomeSecurity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GarageSeguridadFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_garage_seguridad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnGarageOpen = view.findViewById<Button>(R.id.btn_garage_open)
        val btnGarageClose = view.findViewById<Button>(R.id.btn_garage_close)
        val btnSafeModeOn = view.findViewById<Button>(R.id.btn_safe_mode_on)
        val btnSafeModeOff = view.findViewById<Button>(R.id.btn_safe_mode_off)

        btnGarageOpen.setOnClickListener {
            updateDatabase("garage/status", "opening", "Abrió el garage")
        }

        btnGarageClose.setOnClickListener {
            updateDatabase("garage/status", "closing", "Cerró el garage")
        }

        btnSafeModeOn.setOnClickListener {
            updateDatabase("modoSeguro", "on", "Activó el modo seguro")
        }

        btnSafeModeOff.setOnClickListener {
            updateDatabase("modoSeguro", "off", "Desactivó el modo seguro")
        }
    }

    private fun updateDatabase(path: String, value: String, accion: String? = null) {
        dbRef.child(path).setValue(value)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Actualizado: $path -> $value", Toast.LENGTH_SHORT).show()

                // Registrar en el historial si se proporciona una acción
                accion?.let {
                    (activity as? MainActivity)?.registrarHistorial(it)
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun registrarHistorial(accion: String) {
        val historialRef = dbRef.child("historial")
        val nuevoHistorial = mapOf(
            "accion" to accion,
            "usuario" to (FirebaseAuth.getInstance().currentUser?.email ?: "Desconocido"),
            "timestamp" to System.currentTimeMillis().toString()
        )

        historialRef.push().setValue(nuevoHistorial)
            .addOnSuccessListener { /* Historial registrado correctamente */ }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error al registrar historial: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

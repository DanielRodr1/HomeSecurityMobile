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

class LucesFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_luces, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lights = listOf(
            Pair("sala", Pair(R.id.btn_light_on_sala, R.id.btn_light_off_sala)),
            Pair("cocina", Pair(R.id.btn_light_on_cocina, R.id.btn_light_off_cocina)),
            Pair("bano", Pair(R.id.btn_light_on_bano, R.id.btn_light_off_bano)),
            Pair("lavanderia", Pair(R.id.btn_light_on_lavanderia, R.id.btn_light_off_lavanderia)),
            Pair("habitacion", Pair(R.id.btn_light_on_habitacion, R.id.btn_light_off_habitacion)),
            Pair("oficina", Pair(R.id.btn_light_on_oficina, R.id.btn_light_off_oficina)),
            Pair("garage", Pair(R.id.btn_light_on_garage, R.id.btn_light_off_garage))
        )

        for ((area, buttons) in lights) {
            val (btnOnId, btnOffId) = buttons
            val btnOn = view.findViewById<Button>(btnOnId)
            val btnOff = view.findViewById<Button>(btnOffId)

            btnOn.setOnClickListener { updateDatabase("luces/$area", "on", "Encendió luz en $area") }
            btnOff.setOnClickListener { updateDatabase("luces/$area", "off", "Apagó luz en $area") }
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

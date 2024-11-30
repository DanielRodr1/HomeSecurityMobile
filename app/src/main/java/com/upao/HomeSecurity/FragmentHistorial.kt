package com.upao.HomeSecurity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.upao.HomeSecurity.databinding.FragmentHistorialBinding

class FragmentHistorial : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var historialRef: DatabaseReference
    private lateinit var historialAdapter: HistorialAdapter
    private val historialList = mutableListOf<HistorialItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance()
        historialRef = database.getReference("historial")

        // Configuración del RecyclerView
        historialAdapter = HistorialAdapter(historialList)
        binding.recyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistorial.adapter = historialAdapter

        cargarHistorial()
    }

    private fun cargarHistorial() {
        historialRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historialList.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(HistorialItem::class.java)
                    if (item != null) {
                        historialList.add(item)
                    }
                }
                historialAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si es necesario
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

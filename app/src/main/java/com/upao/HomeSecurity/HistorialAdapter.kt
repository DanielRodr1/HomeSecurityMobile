package com.upao.HomeSecurity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class HistorialAdapter(private val historialList: List<HistorialItem>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = historialList[position]
        holder.accionTextView.text = item.accion
        holder.usuarioTextView.text = item.usuario
        holder.timestampTextView.text = item.timestamp
    }

    override fun getItemCount(): Int = historialList.size

    class HistorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val accionTextView: TextView = view.findViewById(R.id.textAccion)
        val usuarioTextView: TextView = view.findViewById(R.id.textUsuario)
        val timestampTextView: TextView = view.findViewById(R.id.textTimestamp)
    }
}

package com.example.gemasmeyerapp_ver2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterDatos(lista: MutableList<String>): RecyclerView.Adapter<AdapterDatos.ViewHolderDatos>() {
    private var listaProductos = lista
    class ViewHolderDatos(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dato = itemView.findViewById<TextView>(R.id.idDato)
        fun asignarDatos(datos: String) {
            dato.text = datos
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderDatos {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list,null,false)
        return ViewHolderDatos(view)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.asignarDatos(listaProductos[position])
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

}
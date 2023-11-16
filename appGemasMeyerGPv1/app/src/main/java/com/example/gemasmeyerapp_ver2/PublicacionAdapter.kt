package com.example.gemasmeyerapp_ver2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.example.gemasmeyerapp_ver2.Models.Publicacion

class PublicacionAdapter(context: Context, resource: Int, objects: MutableList<Publicacion>) :
    ArrayAdapter<Publicacion>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val listItemView = convertView ?: inflater.inflate(R.layout.item_publicacion, parent, false)

        // Obtener la publicación en la posición actual
        val publicacion = getItem(position)

        // Obtener referencias a los elementos de la vista
        val imageView = listItemView.findViewById<ImageView>(R.id.imageView)
        val tituloTextView = listItemView.findViewById<TextView>(R.id.tituloTextView)
        val descripcionTextView = listItemView.findViewById<TextView>(R.id.descripcionTextView)
        imageView.load(publicacion?.imagen)
        // Establecer los valores en los elementos de la vista
        tituloTextView.text = publicacion?.titulo
        descripcionTextView.text = publicacion?.descripcion

        // Aquí debes cargar la imagen desde la URL o recurso y establecerla en el ImageView
        // Ejemplo:
        // Picasso.get().load(publicacion?.imagenUrl).into(imageView)

        return listItemView
    }
}
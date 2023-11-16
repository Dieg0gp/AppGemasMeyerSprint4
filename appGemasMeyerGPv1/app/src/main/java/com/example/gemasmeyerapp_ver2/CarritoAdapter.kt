package com.example.gemasmeyerapp_ver2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.gemasmeyerapp_ver2.Models.ProductoCarrito

class CarritoAdapter(context: Context, productos: MutableList<ProductoCarrito>, listaPedidos: MutableList<ProductoCarrito>) :
    ArrayAdapter<ProductoCarrito>(context, R.layout.item_producto_carrito, productos) {

    // Agregar la lista de pedidos como una propiedad del adaptador
    private val listaPedidos: MutableList<ProductoCarrito> = listaPedidos
    /*
    * modificado todo esto para el boton
    * */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_producto_carrito, parent, false)


        val producto = getItem(position)

        // Configura los elementos de la vista con los datos del producto
        val nombreTextView = view.findViewById<TextView>(R.id.txtNombreProcCarrito)
        val precioTextView = view.findViewById<TextView>(R.id.txtPrecioProcCarrito)
        val cantidadTextView = view.findViewById<TextView>(R.id.txtCantidadProcCarrito)
        val totalxUnidad = view.findViewById<TextView>(R.id.txtTotalProducto)
        val btnQuitarCarrito = view.findViewById<Button>(R.id.btn_QuitarCarrito)

        val precio = producto?.precio ?: 0.0
        val cantidad = producto?.cantidad ?: 0.0
        val total = precio.toDouble() *  cantidad.toInt()

        nombreTextView.text = "Nombre: ${producto?.nombre}"
        precioTextView.text = "Precio: ${producto?.precio}"
        cantidadTextView.text = "Cantidad: ${producto?.cantidad}"
        totalxUnidad.text = "Total: $total"

        // Configura un OnClickListener para el botón de quitar carrito
        btnQuitarCarrito.setOnClickListener {
            // Elimina el producto de la lista de pedidos
            listaPedidos.remove(producto)
            // Notifica al adaptador de que los datos han cambiado
            notifyDataSetChanged()
        }
        return view
    }
}


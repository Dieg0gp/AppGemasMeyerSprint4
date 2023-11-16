package com.example.gemasmeyerapp_ver2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gemasmeyerapp_ver2.Data.Constantes
import com.example.gemasmeyerapp_ver2.Models.Producto
import com.example.gemasmeyerapp_ver2.Models.ProductoCarrito
import com.example.gemasmeyerapp_ver2.Views.StoreFragment
import com.example.gemasmeyerapp_ver2.databinding.FragmentStoreBinding
import android.widget.Toast

private const val ARG_PARAM1 = "productos"
private const val ARG_PARAM2 = "precioProducto"
class AdaptadorProductos(listaP: MutableList<Producto>,storeFragment: FragmentStoreBinding): RecyclerView.Adapter<AdaptadorProductos.ViewHolderProductos>() {
    private var montoTotal = 0
    private var listaProductos = listaP
    private var actividadFragmentStore = storeFragment
    private val datosDetalleCompra = mutableListOf<String>()
    private lateinit var adaptadorListaDetalles : ArrayAdapter<String>
    private lateinit var productoCarrito : ProductoCarrito
    private lateinit var productos : ArrayList<String>
    private val listaProductoCarrito = mutableListOf<ProductoCarrito>()
    private var nuevaCantidad = 1
    private var total = 0
    private var bundle = Bundle()

    class ViewHolderProductos(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoNombre = itemView.findViewById<TextView>(R.id.txtNombreProducto)
        val textoPrecio = itemView.findViewById<TextView>(R.id.txtPrecio)
        val imagen = itemView.findViewById<ImageView>(R.id.imgProducto)
        val prBarImagen = itemView.findViewById<ProgressBar>(R.id.prBarImagen)
        val stock = itemView.findViewById<TextView>(R.id.txtStock)
        val cantidad = itemView.findViewById<TextView>(R.id.txtCantidad)
        val btnSumarCantidad = itemView.findViewById<Button>(R.id.btnAdicionar)
        val btnRestarCantidad = itemView.findViewById<Button>(R.id.btnRestar)
        val btnAgregarProducto = itemView.findViewById<Button>(R.id.btnAgregar)
        val btnborrar = itemView.findViewById<Button>(R.id.btnBorrarProcCarrito)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderProductos {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_productos,null,false)
        adaptadorListaDetalles = ArrayAdapter(actividadFragmentStore.root.context, R.layout.item_producto_carrito, datosDetalleCompra)
        //actividadFragmentStore.listProductos.adapter = adaptadorListaDetalles
        productos = arrayListOf()
        return ViewHolderProductos(vista)
    }

    override fun getItemCount(): Int = listaProductos.size

    override fun onBindViewHolder(holder: ViewHolderProductos, position: Int) {
        holder.textoNombre.text = listaProductos[position].nombre
        //Uso de la biblioteca coil para cargar la imagen por medio de una url y una progress bar
        val idImagen = listaProductos[position].imagen.substringAfterLast("uc?id=")
        val urlImagen = "https://drive.google.com/uc?export=view&id=$idImagen"
        holder.prBarImagen.visibility = View.VISIBLE
        holder.imagen.load(urlImagen) {
            listener(onError = { request, throwable ->
                //Si la carga de imagen falla se carga la imagen de repuesto que se ha descargado
                holder.imagen.load(listaProductos[position].imagenRepuesto){
                    listener(onSuccess = { _, _ ->
                        holder.prBarImagen.visibility = View.GONE // Ocultar ProgressBar determinado una vez que la imagen se haya cargado
                    })
                }
                //val errorMessage = throwable.message ?: Constantes.showAlert(holder.itemView.context,"Error","Error desconocido",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                //Constantes.showAlert(holder.itemView.context,"Error","Error en la carga de imagen: $errorMessage",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }, onSuccess = { _, _ ->
                holder.prBarImagen.visibility = View.GONE // Ocultar ProgressBar determinado una vez que la imagen se haya cargado
            })
        }

        holder.textoPrecio.text = "Precio: Bs. ${listaProductos[position].precio}"
        var is_zoom_in = false
        holder.stock.text = "Quedan: ${listaProductos[position].cantidad} ejemplares"

        holder.btnSumarCantidad.setOnClickListener {
            if(nuevaCantidad<listaProductos[position].cantidad) {
                nuevaCantidad = nuevaCantidad.plus(1)
            }
            holder.cantidad.text = nuevaCantidad.toString()
        }
        holder.btnRestarCantidad.setOnClickListener {
            if(nuevaCantidad>1)
            {
                nuevaCantidad = nuevaCantidad.minus(1)
            }
            holder.cantidad.text = nuevaCantidad.toString()
        }
        //Animación imagen
        holder.imagen.setOnClickListener {
            if(!is_zoom_in)
            {
                holder.textoNombre.visibility = View.INVISIBLE
                holder.textoPrecio.visibility = View.INVISIBLE
                it.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,R.anim.zoom_in))
                is_zoom_in = true
            }
            else {
                it.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,R.anim.zoom_out))
                holder.textoNombre.visibility = View.VISIBLE
                holder.textoPrecio.visibility = View.VISIBLE
                is_zoom_in = false
            }
            Toast.makeText(holder.itemView.context,"Imagen presionada", Toast.LENGTH_SHORT).show()
        }
        //Agregar productos
        holder.btnAgregarProducto.setOnClickListener {
            //Pasar el id del producto seleccionado
            val st = FragmentManager.findFragment<StoreFragment>(actividadFragmentStore.root)
            val args = Bundle()
            productoCarrito = ProductoCarrito(listaProductos[position].idProducto,listaProductos[position].nombre,
                nuevaCantidad,
                listaProductos[position].precio)
            args.putParcelable(ARG_PARAM1,productoCarrito)
            st.arguments = args

            if(!listaProductoCarrito.contains(ProductoCarrito(listaProductos[position].idProducto,listaProductos[position].nombre,nuevaCantidad, listaProductos[position].precio))) {
                adaptadorListaDetalles.add(
                    """
                ---------------------------------
                Producto: ${listaProductos[position].nombre}
                Precio: ${listaProductos[position].precio}
                Cantidad: $nuevaCantidad

            """.trimIndent()
                )
                listaProductoCarrito.add(
                    ProductoCarrito(
                        listaProductos[position].idProducto,
                        listaProductos[position].nombre,
                        nuevaCantidad,
                        listaProductos[position].precio
                    )
                )

                total += nuevaCantidad * listaProductos[position].precio
                //actividadFragmentStore.txtTotal.text = "Total: Bs.${total}"
                Toast.makeText(holder.itemView.context,"Agregado al Carrito", Toast.LENGTH_SHORT).show()
                //Toast.makeText(applicationContext, "Agregado al Carrito", Toast.LENGTH_LONG).show()
            }
            else
            {
                Constantes.showAlert(actividadFragmentStore.root.context,"Advertencia ⚠","""
                    Usted ya ha seleccionado el producto y no puede volver a seleccionarlo.
                    Si usted desea agregar mas productos debe borrar su selección actual y volver a seleccionar el producto con una cantidad.
                """.trimIndent(),Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
        }
    }
}
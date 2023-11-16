package com.example.gemasmeyerapp_ver2.Views

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gemasmeyerapp_ver2.AdaptadorProductos
import com.example.gemasmeyerapp_ver2.CarritoAdapter
import com.example.gemasmeyerapp_ver2.Data.Constantes
import com.example.gemasmeyerapp_ver2.Data.PedidoRepository
import com.example.gemasmeyerapp_ver2.Data.ProductoRepository
import com.example.gemasmeyerapp_ver2.Data.UsuarioRepository
import com.example.gemasmeyerapp_ver2.DescargarImagenes
import com.example.gemasmeyerapp_ver2.Models.Pedido
import com.example.gemasmeyerapp_ver2.Models.Producto
import com.example.gemasmeyerapp_ver2.Models.ProductoCarrito
import com.example.gemasmeyerapp_ver2.Models.Usuario
import com.example.gemasmeyerapp_ver2.R.*
import com.example.gemasmeyerapp_ver2.databinding.FragmentStoreBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "productos"
private const val ARG_PARAM2 = "precioProducto"
private const val ARG_PARAM3 = "cantidadProducto"
/**
 * A simple [Fragment] subclass.
 * Use the [StoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StoreFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var repositorioProductos : ProductoRepository
    private var respuestaRegistro : Boolean? = null
    private lateinit var listaPedidos : MutableList<ProductoCarrito>
    private lateinit var customDialogView : View
    private lateinit var binding: FragmentStoreBinding
    private lateinit var usuario: Usuario
    private lateinit var listaProductos: MutableList<Producto>
    private lateinit var listaUsuarios: MutableList<Usuario>
    private lateinit var producto: Producto
    private var cantidadProducto: Int? = null
    private var fechaActual: String? = null
    private var fechaExpiracion: String? = null
    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    //calcular fecha actual
    @SuppressLint("SimpleDateFormat")
    fun obtenerFechaActualEnFormatoDeseado(): String {
        val calendario = Calendar.getInstance()
        val formatoDeseado = SimpleDateFormat("yyyy-MM-dd")
        return formatoDeseado.format(calendario.time)
    }
    //La fecha de expiración esta programada con tres días
    @SuppressLint("SimpleDateFormat")
    fun obtenerFechaActualMasTresDias(): String {
        val calendario = Calendar.getInstance()
        val formatoDeseado = SimpleDateFormat("yyyy-MM-dd")
        // Suma tres días a la fecha actual
        calendario.add(Calendar.DAY_OF_MONTH, 3)
        return formatoDeseado.format(calendario.time)
    }
    //En caso de querer refrescar un fragmento
    fun refrescarFragmento() {
       context.let {
           val fragmentManager = (context as AppCompatActivity).supportFragmentManager
           fragmentManager.let {
               val fragmentoActual = this
               fragmentoActual.let {
                   val fragmentTransaction = fragmentManager.beginTransaction()
                   fragmentTransaction.detach(it)
                   fragmentTransaction.attach(it)
                   fragmentTransaction.commit()
               }
           }
       }
    }
    //@RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //Recuperar los productos del cliente
        listaPedidos = mutableListOf()
        recuperarProductosCarrito()
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        //Modifique para que se pueda borrar de la lista esta parte de aca
        val adapter = CarritoAdapter(requireContext(), listaPedidos, listaPedidos)
        val btnPedir = customDialogView.findViewById<Button>(com.example.gemasmeyerapp_ver2.R.id.btnPedirProcCarrito)
        val btnBorrar = customDialogView.findViewById<Button>(com.example.gemasmeyerapp_ver2.R.id.btnBorrarProcCarrito)
        poblarLista()
        obtenerUsuarioApi()
        //binding.txtTotal.text = "Total: ${0}"
        binding.btnAr.setOnClickListener {
            abrirAplicacion("edu.univalle.siumovil")
        }
        binding.imgBtnCarrito.setOnClickListener {
            val customDialogView2 : View = layoutInflater.inflate(layout.item_list_carrito, null)
            customDialogView = customDialogView2
            val btnBorrar = customDialogView.findViewById<Button>(com.example.gemasmeyerapp_ver2.R.id.btnBorrarProcCarrito)
            val btnPedir = customDialogView.findViewById<Button>(com.example.gemasmeyerapp_ver2.R.id.btnPedirProcCarrito)
            val listaProductosCarrito = customDialogView2.findViewById<ListView>(com.example.gemasmeyerapp_ver2.R.id.listPedidos)
            listaProductosCarrito.adapter = adapter
            if (listaPedidos.isNotEmpty()) {
                Constantes.showAlert(binding.root.context, "Carrito", "", Constantes.Companion.TIPO_ALERTA.CARRITO, customDialogView2)
                listaProductosCarrito.visibility = View.VISIBLE
            } else {
                Constantes.showAlert(binding.root.context, "Carrito", "El carrito está vacío", Constantes.Companion.TIPO_ALERTA.CARRITO, customDialogView2)
                listaProductosCarrito.visibility = View.GONE
            }
            val objetoRecibido = arguments?.getParcelable<ProductoCarrito>(ARG_PARAM1)
            if (objetoRecibido != null) {
                var productoExistente = false
                for (pedidoExistente in listaPedidos) {
                    if (pedidoExistente.idProducto == objetoRecibido.idProducto) {
                        productoExistente = true
                        break
                    }
                }
                if (!productoExistente) {
                    listaPedidos.add(objetoRecibido)
                    adapter.notifyDataSetChanged()
                }
            }
            btnBorrar.setOnClickListener {
                listaPedidos.clear()
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(),"Productos eliminados",Toast.LENGTH_SHORT).show()
            }
            btnPedir.setOnClickListener {
                fechaActual = obtenerFechaActualEnFormatoDeseado()
                if(listaPedidos.size != 0)
                {
                    listaPedidos.forEach {
                        val pedidoNuevo = Pedido(null,usuario.ci,it.idProducto,3, it.cantidad,fechaActual!!)
                        val pedidoRepository = PedidoRepository()
                        val llamadaPedido = pedidoRepository.registrarPedido(pedidoNuevo)
                        llamadaPedido.enqueue(object : Callback<Boolean> {
                            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                if (response.isSuccessful) {
                                    // Solicitud exitosa, maneja la respuesta aquí
                                    Constantes.showAlert(binding.root.context,"Mensaje","Pedido realizado con éxito",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                                    respuestaRegistro = response.body()
                                } else {
                                    Constantes.showAlert(binding.root.context,"Error","Se ha producido un error al realizar el pedido",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                                }
                            }
                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                Constantes.showAlert(binding.root.context,"Error de servidor","Se ha producido un error: $t",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                            }
                        })
                    }
                }
            }
        }
        setUpRecylcerView()

        return binding.root
    }

    private fun recuperarProductosCarrito() {
        customDialogView = layoutInflater.inflate(layout.item_list_carrito, null)

        //Obtener el producto desde el adaptador
    }

    private fun abrirAplicacion(packageName: String) {
        val pm: PackageManager = requireActivity().packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)

        if (intent != null) {
            startActivity(intent)
        } else {
            // La aplicación no está instalada, o el paquete no se encuentra
            // Puedes dirigir al usuario a la página de la aplicación en Google Play Store
            val playStoreIntent = Intent(Intent.ACTION_VIEW)
            playStoreIntent.data = Uri.parse("https://play.google.com/store/apps/details?id=edu.univalle.siumovil&pcampaignid=web_share")
            startActivity(playStoreIntent)
        }
    }


    private fun openExternalApkFile() {

        /*val apkFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "esteeselnuevo.apk")
        val apkUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", apkFile)
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(installIntent)*/
        // Create an Intent with action ACTION_INSTALL_PACKAGE
        lifecycleScope.launch {
            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)

            // Set the data URI to the APK file's path
            val apkUri = Uri.fromFile(
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "2021AURP_base.apk"
                )
            )
            intent.data = apkUri

            // Optionally, set flags based on your requirements
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

            // Check the Android version to handle file URIs correctly
            // For Android Nougat (API level 24) and higher, use FileProvider
            val fileProviderUri = FileProvider.getUriForFile(
                binding.root.context,
                binding.root.context.packageName + ".provider",
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "esteeselnuevo.apk"
                )
            )
            intent.data = fileProviderUri

            // Start the package installer
            startActivity(intent)
        }
    }
    private fun obtenerUsuarioApi() {
        val repositorioUsuarios = UsuarioRepository()
        //recuperar usuarios
        lifecycleScope.launch {
            val usuariosJson = repositorioUsuarios.obtenerUsuarios()
            listaUsuarios =
                gson.fromJson(usuariosJson, object : TypeToken<MutableList<Usuario>>() {}.type)
            val prefs =  requireContext().getSharedPreferences(getString(string.prefs_file),
                AppCompatActivity.MODE_PRIVATE
            )
            //recuperar el usuario con el email
            usuario = listaUsuarios.firstOrNull { it.correo.equals(prefs.getString("email",null)) }!!
        }
    }
    private fun poblarLista() {
        repositorioProductos = ProductoRepository()
        lifecycleScope.launch {
            val productosJson = repositorioProductos.obtenerProductosEnStock()
            listaProductos = gson.fromJson(productosJson, object : TypeToken<MutableList<Producto>>() {}.type)
            if(listaProductos.size != 0)
            {
                //verificar si se ha añadido una nueva imagen
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                val productos = sharedPreferences.getInt(Constantes.CANTIDAD_PRODUCTOS_EN_STOCK_KEY, listaProductos.size)
                //descargar imagenes
                val descargadorImagenes = DescargarImagenes()
                // Verificar si la cantidad de elementos en listaProductos ha aumentado
                if (listaProductos.size > productos) {
                    // La cantidad de elementos en listaProductos ha aumentado
                    descargadorImagenes.downloadImage(binding.root.context,listaProductos.last().imagen,listaProductos.last().nombre)
                    listaProductos.last().imagenRepuesto = descargadorImagenes.obtenerImagen(binding.root.context,listaProductos.last().nombre)
                    // Actualizar el valor almacenado en SharedPreferences con la nueva cantidad
                    val editor = sharedPreferences.edit()
                    editor.putInt(Constantes.CANTIDAD_PRODUCTOS_EN_STOCK_KEY, listaProductos.size)
                    editor.apply()
                }
                else
                {
                    val editor = sharedPreferences.edit()
                    editor.putInt(Constantes.CANTIDAD_PRODUCTOS_EN_STOCK_KEY, listaProductos.size)
                    editor.apply()
                }
                //descargadorImagenes.mostrarArchivosEnMemoria(binding.root.context)
                //Este código descarga la imagen y luego la introduce en la propiedad del objeto llamada imagenRepuesto
                listaProductos.forEach {
                    //Si se ha agregado una nueva imagen utilizar la función downloadImage
                    //descargadorImagenes.downloadImage(binding.root.context,it.imagen,it.nombre)
                    it.imagenRepuesto = descargadorImagenes.obtenerImagen(binding.root.context,it.nombre)
                }
                //descargadorImagenes.mostrarArchivosEnMemoria(binding.root.context)
                //binding.imgPrueba.setImageBitmap(descargadorImagenes.obtenerImagen(binding.root.context,listaProductos[0].nombre))
                val adaptadorDatos = AdaptadorProductos(listaProductos, binding)
                binding.rcView.adapter = adaptadorDatos
            }
            else
            {
                Constantes.showAlert(binding.root.context,"Mensaje","Actualmente no tenemos productos disponibles 🙁",Constantes.Companion.TIPO_ALERTA.ALERTA_POS_NEG)
            }
        }
    }

    private fun setUpRecylcerView() {
        binding.rcView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        //binding.rcView.layoutManager = GridLayoutManager(context,1)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StoreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
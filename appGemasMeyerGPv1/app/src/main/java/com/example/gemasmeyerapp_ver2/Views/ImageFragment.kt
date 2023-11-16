package com.example.gemasmeyerapp_ver2.Views

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.client.OpenAI
import com.example.gemasmeyerapp_ver2.Data.*
import com.example.gemasmeyerapp_ver2.Models.Peticion
import com.example.gemasmeyerapp_ver2.Models.SubirImagen
import com.example.gemasmeyerapp_ver2.Models.Usuario
import com.example.gemasmeyerapp_ver2.R
import com.example.gemasmeyerapp_ver2.databinding.FragmentImageBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/* dependencias sin usar
import com.example.gemasmeyerapp_ver2.databinding.FragmentVrBinding
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.HitResult
import com.google.ar.sceneform.ux.ArFragment*/

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var respuestaRegistro : Boolean? = null
    private lateinit var imagenes : List<ImageURL>
    //private val CAMERA_REQUEST = 1888
    //private lateinit var arFragment : ArFragment
    private lateinit var binding: FragmentImageBinding
    private lateinit var usuario: Usuario
    private lateinit var listaUsuarios: MutableList<Usuario>
    val gson = Gson()
    private lateinit var peticion: Peticion
    private lateinit var imagenURL : String
    private lateinit var deepAI : DeepIARepository
    private lateinit var subirImagenRepo : SubirImagenRepository
    //uso openAI
    private var openAI : OpenAI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        //arFragment = ArFragment()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun obtenerUsuarioApi() {
        val repositorioUsuarios = UsuarioRepository()
        //recuperar usuarios
        lifecycleScope.launch {
            val usuariosJson = repositorioUsuarios.obtenerUsuarios()
            listaUsuarios =
                gson.fromJson(usuariosJson, object : TypeToken<MutableList<Usuario>>() {}.type)
            val prefs =  requireContext().getSharedPreferences(getString(R.string.prefs_file),
                AppCompatActivity.MODE_PRIVATE
            )
            //recuperar el usuario con el email
            usuario = listaUsuarios.firstOrNull { it.correo.equals(prefs.getString("email",null)) }!!
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        obtenerUsuarioApi()
        //ocultar antes de generar la imagen
        ocultarMostrarPedido(View.GONE)
        //Constantes.showAlert(requireContext(),"Mensaje","Usted puede generar únicamente 10 imágenes por mes. \n si le gusta una imagen, guarde la imagen y envíela al recepcionista con el botón realizar pedido para que el recepcionista considere su pedido",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
        binding.prBarImagenNuevaJoya.visibility = View.INVISIBLE
        openAI = OpenAI(Constantes.API_KEY_OPENAI)
        var cantidad = binding.etCantidadPedNuevaJoya.text.toString().toInt()
        binding.btnAdicionarPedNuevaJoya.setOnClickListener {
            if(cantidad < 3) {
                cantidad = cantidad.plus(1)
            }
            binding.etCantidadPedNuevaJoya.text = cantidad.toString()
        }
        binding.btnRestarPedNuevaJoya.setOnClickListener {
            if(cantidad > 0)
            {
                cantidad = cantidad.minus(1)
            }
            binding.etCantidadPedNuevaJoya.text = cantidad.toString()
        }
        binding.btnDescargarNuevaJoya.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(imagenes.firstOrNull()?.url))
            startActivity(browserIntent)
            imagenURL = Uri.parse(imagenes.firstOrNull()?.url).toString()
        }
        binding.btnGenerar.setOnClickListener {
            if(binding.etPrompt.text.isNotEmpty() && binding.etEspecificaciones.text.isNotEmpty()) {
                generarImagen()
            }
            else
            {
                Constantes.showAlert(requireContext(),"Advertencia ⚠","Por favor ingrese un título y las especificaciones para la nueva joya",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
        }
        binding.btnPedirNuevaJoya.setOnClickListener {
            if(binding.etPrompt.text.isNotEmpty() && binding.etEspecificaciones.text.isNotEmpty() && imagenURL.isNotEmpty()) {
                //Constantes.showAlert(binding.root.context,"Imagen base 64","",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                peticion = Peticion(idUsuario = usuario.ci, productoNombre = binding.etPrompt.text.toString(), imagen = "", cantidad = cantidad, especificaciones = binding.etEspecificaciones.text.toString(), estado = 0)
                val pedidoRepository = PedidoRepository()
                val llamadaPeticion = pedidoRepository.registrarPeticion(peticion)
                llamadaPeticion.enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        if (response.isSuccessful) {
                            // Solicitud exitosa, maneja la respuesta aquí
                            Constantes.showAlert(binding.root.context,"Mensaje","Petición realizada con éxito",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                            respuestaRegistro = response.body()
                        } else {
                            Constantes.showAlert(binding.root.context,"Error","Se ha producido un error al realizar la petición: ${peticion}",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                        }
                    }
                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        Constantes.showAlert(binding.root.context,"Error de servidor","Se ha producido un error: $t",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                    }
                })
                Constantes.showAlert(requireContext(),"Mensaje","Se le notificará por celular sobre el estado de su nueva petición, si esta fue aprobada o denegada",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
        }
        /* Sin usar
        //Permisos para la cámara
        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED) {
            // Si la aplicación no tiene permisos, se solicitan
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)
        } else {
            // Si la aplicación ya tiene permisos, se puede acceder a la cámara
            //binding.sceneAr.setBackgroundColor(Color.TRANSPARENT)
            //Se carga el modelo
            val frag = binding.uxFragment.getFragment<Fragment>()
            arFragment = ArFragment()
            arFragment = frag as ArFragment

        }*/
        return binding.root
    }
    //Función que puede mostrar o ocultar la interfaz para pedir una joya personalizada
    private fun ocultarMostrarPedido(op: Int) {
        binding.txtCantidadPedidoNuevaJoya.visibility = op
        binding.btnPedirNuevaJoya.visibility = op
        binding.btnAdicionarPedNuevaJoya.visibility = op
        binding.btnDescargarNuevaJoya.visibility = op
        binding.btnRestarPedNuevaJoya.visibility = op
        binding.etCantidadPedNuevaJoya.visibility = op
    }
    fun copiarAlPortapapeles(context: Context, texto: String) {
        // Obtiene el servicio del portapapeles
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Crea un objeto ClipData para almacenar el texto
        val clipData = ClipData.newPlainText("TextoCopiado", texto)

        // Coloca el ClipData en el portapapeles
        clipboardManager.setPrimaryClip(clipData)
    }
    private fun generarImagen() {
        lifecycleScope.launch {
            try {
                binding.prBarImagenNuevaJoya.visibility = View.VISIBLE
                val prompt = binding.etPrompt.text.toString() + ", ${binding.etEspecificaciones.text}"
                //ocultar antes de generar la imagen
                ocultarMostrarPedido(View.GONE)
                imagenes = openAI?.imageURL(ImageCreation(prompt,1, ImageSize.is1024x1024))!!
                binding.imgNuevaJoya.load(imagenes.firstOrNull()?.url){
                    listener(
                        onError = { request, throwable ->
                        binding.prBarImagenNuevaJoya.visibility = View.GONE // Ocultar ProgressBar determinado una vez que la imagen se haya cargado
                        Constantes.showAlert(requireContext(),"Error","Error en la generación de la imagen, ${throwable}",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                    }, onSuccess = { _, _ ->
                        binding.prBarImagenNuevaJoya.visibility = View.GONE // Ocultar ProgressBar determinado una vez que la imagen se haya cargado
                        //mostrar opción para pedir
                            ocultarMostrarPedido(View.VISIBLE)
                    })
                }
            } catch (e: Exception) {
                Constantes.showAlert(requireContext(),"Mensaje","${e.cause?.cause}",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
            //Constantes.showAlert(requireContext(),"Mensaje","Generando imagen",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
        }
    }

    private fun subirImagen(imagenUrl: String) {
        subirImagenRepo = SubirImagenRepository()
        val ImagenLista = SubirImagen(Constantes.API_KEY_IMG_BB, imagenUrl)
        val llamadaPeticionSubirImagen = subirImagenRepo.subirImagen(ImagenLista)
        llamadaPeticionSubirImagen.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    // Solicitud exitosa, maneja la respuesta aquí
                    Constantes.showAlert(binding.root.context,"Mensaje","Petición realizada con éxito",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                    Constantes.showAlert(requireContext(),"Response",response.body().toString(),Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                } else {
                    Constantes.showAlert(binding.root.context,"Error","Se ha producido un error al realizar la petición: ${response.code()}",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                }
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Constantes.showAlert(binding.root.context,"Error de servidor","Se ha producido un error: $t",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
        })
    }
    /*deepAI
    private fun generateImage(prompt: String) {
        deepAI = DeepIARepository()
        val promptText = DeepAIRequest(binding.etPrompt.text.toString())
        val call = deepAI.generateImage("", promptText)

        call.enqueue(object : Callback<DeepAIResponse> {
            override fun onResponse(
                call: Call<DeepAIResponse>,
                response: Response<DeepAIResponse>
            ) {
                if (response.isSuccessful) {
                    val deepAIResponse = response.body()
                    Constantes.showAlert(requireContext(),"Mensaje",deepAIResponse.toString(),Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                } else {
                    Constantes.showAlert(requireContext(),"Error",response.toString(),Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                }
            }

            override fun onFailure(call: Call<DeepAIResponse>, t: Throwable) {
                Constantes.showAlert(requireContext(),"Error",t.message.toString(),Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
        })
    }*/
    //@RequiresApi(Build.VERSION_CODES.N)
    /*private fun createModelRenderable(hitResult: HitResult) {
        val anchor: Anchor = hitResult.createAnchor()

    }*/

    //Comprobar si el dispositivo es compatible
    /*
    override fun onResume() {
        super.onResume()
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        if (availability.isTransient) {
            // ARCore is currently being updated, wait until it's ready.
        } else {
            if (availability.isSupported) {
                //Constantes.showAlert(requireContext(),"Dispositivo compatible","Su dispositivo es compatible con realidad aumentada",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                //Session(requireContext())
                // ARCore is supported on this device, proceed with ARCore app logic.
            } else {
                // ARCore is not supported on this device.
                Constantes.showAlert(requireContext(),"Dispositivo no compatible","Su dispositivo no es compatible con realidad aumentada",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
        }
    }*/

}
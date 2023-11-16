package com.example.gemasmeyerapp_ver2

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.gemasmeyerapp_ver2.Data.PublicacionesRepository
import com.example.gemasmeyerapp_ver2.Models.Publicacion
import com.example.gemasmeyerapp_ver2.databinding.FragmentPublicidadBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class publicidadFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var repositorioPublicaciones : PublicacionesRepository
    private lateinit var listaPublicaciones: MutableList<Publicacion>
    val gson = Gson()
    private lateinit var binding: FragmentPublicidadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentPublicidadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obtenerPublicaciones()
    }

    private fun obtenerPublicaciones() {
        val contexto = requireContext() // Obtener el contexto del fragmento

        // Crear el repositorio de publicaciones (puedes considerar la inyección de dependencias aquí)
        repositorioPublicaciones = PublicacionesRepository()

        // Usar lifecycleScope del fragmento en lugar del de la actividad
        lifecycleScope.launch {
            try {
                // Obtener las publicaciones de forma asíncrona
                val publicacionesJson = repositorioPublicaciones.obtenerPublicaciones()

                // Parsear JSON y actualizar la UI
                listaPublicaciones = gson.fromJson(publicacionesJson, object : TypeToken<MutableList<Publicacion>>() {}.type)
                val adapter = PublicacionAdapter(contexto, R.layout.item_publicacion, listaPublicaciones)
                binding.listViewPublicaciones.adapter = adapter
            } catch (e: Exception) {
                // Manejar errores aquí, por ejemplo, mostrar un mensaje al usuario
                Log.e(ContentValues.TAG, "Error al obtener publicaciones: ${e.message}", e)
            }
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
            publicidadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
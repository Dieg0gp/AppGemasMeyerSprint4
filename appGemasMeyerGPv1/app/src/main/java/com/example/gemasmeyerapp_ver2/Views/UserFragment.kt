package com.example.gemasmeyerapp_ver2.Views

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gemasmeyerapp_ver2.Data.UsuarioRepository
import com.example.gemasmeyerapp_ver2.Models.Usuario
import com.example.gemasmeyerapp_ver2.R
import com.example.gemasmeyerapp_ver2.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var usuario: Usuario
    private lateinit var repositorioUsuarios : UsuarioRepository
    private lateinit var listaUsuarios: MutableList<Usuario>
    val gson = Gson()
    private lateinit var binding: FragmentUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater, container, false)
        val prefs =  requireContext().getSharedPreferences(getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        obtenerUsuarioApi()
        binding.txtEmail.text = prefs.getString("email",null)
        binding.btnLogout.setOnClickListener {
            pasarPantalla(MainActivity::class.java)
            FirebaseAuth.getInstance().signOut()
        }
        return binding.root
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
            binding.txtNombre.text = usuario.nombreCompleto
            val tipo = if (usuario.tipo == 1) "Administrador" else if (usuario.tipo == 2) "Vendedor" else "Cliente"
            binding.txtTipo.text = tipo
        }
    }
    private fun pasarPantalla(actividad: Class<*>) {
        val intencion = Intent(context,actividad).apply {
            putExtra("borrar",true)
        }
        startActivity(intencion)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
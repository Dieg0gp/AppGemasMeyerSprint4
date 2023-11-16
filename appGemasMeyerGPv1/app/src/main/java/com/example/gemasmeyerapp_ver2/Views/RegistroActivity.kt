package com.example.gemasmeyerapp_ver2.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.gemasmeyerapp_ver2.Data.Constantes
import com.example.gemasmeyerapp_ver2.Data.UsuarioRepository
import com.example.gemasmeyerapp_ver2.Models.Usuario
import com.example.gemasmeyerapp_ver2.Permisos
import com.example.gemasmeyerapp_ver2.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroActivity : AppCompatActivity() {
    private lateinit var ci: String
    private lateinit var nombre: String
    private val estado :Int = 1
    private var respuestaRegistro : Boolean? = null
    private lateinit var email: String
    private lateinit var celular: String
    private lateinit var clave: String
    private lateinit var binding: ActivityRegistroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //comprobar permisos
        val permisos = Permisos(this)
        //si el dispositivo no esta conectado a internet muestra un mensaje
        if(!permisos.comprobarConexionInternet())
            permisos.mostrarConexionInternetRequerida()
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAtrasRegistro.setOnClickListener {
           Constantes.pasarPantalla(this,MainActivity::class.java)
        }
        hideStatusBar()
        //Setup
        registro()
    }
    private fun validarCampos() : Boolean {
        return (Constantes.validarCI(binding.etCi.text.toString(),binding.etCi) &&
                Constantes.validarCorreoElectronico(binding.etEmailRegistro.text.toString(),binding.etEmailRegistro) &&
                Constantes.validarCelular(binding.etCelularRegistro.text.toString(),binding.etCelularRegistro) &&
                Constantes.validarClave(binding.etClaveRegistro.text.toString(),binding.etClaveRegistro) &&
                Constantes.validarNombre(binding.etNombre.text.toString(),binding.etNombre))
    }
    private fun registro() {
        
        binding.etCi.addTextChangedListener {
            Constantes.validarCI(binding.etCi.text.toString(),binding.etCi)
        }
        binding.etEmailRegistro.addTextChangedListener {
            Constantes.validarCorreoElectronico(binding.etEmailRegistro.text.toString(),binding.etEmailRegistro)
        }
        binding.etCelularRegistro.addTextChangedListener {
            Constantes.validarCelular(binding.etCelularRegistro.text.toString(),binding.etCelularRegistro)
        }
        binding.etClaveRegistro.addTextChangedListener {
            Constantes.validarClave(binding.etClaveRegistro.text.toString(),binding.etClaveRegistro)
        }
        binding.etNombre.addTextChangedListener {
            Constantes.validarNombre(binding.etNombre.text.toString(),binding.etNombre)
        }
        binding.btnRegistrarse.setOnClickListener {
            val contexto = this
            if(camposValidos())
            {
                if(validarCampos())
                {
                    ci = binding.etCi.text.toString()
                    email = binding.etEmailRegistro.text.toString()
                    clave = binding.etClaveRegistro.text.toString()
                    nombre = binding.etNombre.text.toString()
                    //Registro API
                    val usuarioNuevo = Usuario(ci,clave,email,celular,3,estado,nombre)
                    val usuarioRepository = UsuarioRepository()
                    val llamadaRegistro = usuarioRepository.registrarUsuario(usuarioNuevo)
                    llamadaRegistro.enqueue(object : Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if (response.isSuccessful) {
                                // Solicitud exitosa, maneja la respuesta aquí
                                Constantes.showAlert(contexto,"Registro exitoso","Se ha registrado correctamente",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                                respuestaRegistro = response.body()
                                //Registro a fire base
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,clave).addOnCompleteListener {
                                    if(it.isSuccessful && respuestaRegistro == true)
                                    {
                                        Constantes.pasarPantallaHome(contexto, HomeActivity::class.java,it.result?.user?.email,
                                            ProviderType.BASIC)
                                    }
                                    else
                                    {
                                        Constantes.showAlert(contexto,"Error","Se ha producido un error al autenticar al usuario",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                                    }
                                }
                            } else {
                                Constantes.showAlert(contexto,"Error","Se ha producido un error",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                            }
                        }
                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            Constantes.showAlert(contexto,"Error","Se ha producido un error: $t",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                        }
                    })
                }
                else
                {
                    Constantes.showAlert(contexto,"Error","Debe asegurarse de que todos los campos introducidos son correctos",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
                }
            }
            else
            {
                Constantes.showAlert(this,"Error","Algún campo no ha sido llenado",Constantes.Companion.TIPO_ALERTA.ALERTA_SIMPLE)
            }
        }
    }

    private fun camposValidos(): Boolean {
        return binding.etCi.text.isNotEmpty() && binding.etEmailRegistro.text.isNotEmpty() && binding.etClaveRegistro.text.toString().isNotEmpty() && binding.etNombre.text.isNotEmpty()
    }

    private fun hideStatusBar() {
        supportActionBar?.hide()
    }
}
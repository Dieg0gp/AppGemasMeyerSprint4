package com.example.gemasmeyerapp_ver2.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.gemasmeyerapp_ver2.Data.ProductoRepository
import com.example.gemasmeyerapp_ver2.Data.UsuarioRepository
import com.example.gemasmeyerapp_ver2.Models.Usuario
import com.example.gemasmeyerapp_ver2.Permisos
import com.example.gemasmeyerapp_ver2.R
import com.example.gemasmeyerapp_ver2.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    //Identificador google
    private val GOOGLE_SIGN_IN = 100
    private lateinit var email: String
    private lateinit var clave: String
    private lateinit var repositorioUsuarios : UsuarioRepository
    private lateinit var listaUsuarios: MutableList<Usuario>
    val gson = Gson()
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //obtener usuarios
        obtenerUsuariosApi()
        //comprobar permisos
        val permisos = Permisos(this)
        //si el dispositivo no esta conectado a internet muestra un mensaje
        if(!permisos.comprobarConexionInternet())
            permisos.mostrarConexionInternetRequerida()
        hideStatusBar()
        signInFirebase()
        signInGoogle()
        binding.btnAtrasLogin.setOnClickListener {
            pasarPantalla(MainActivity::class.java)
        }
    }

    private fun obtenerUsuariosApi() {
        val repositorioUsuarios = UsuarioRepository()
        lifecycleScope.launch {
            val usuariosJson = repositorioUsuarios.obtenerUsuarios()
            listaUsuarios =
                gson.fromJson(usuariosJson, object : TypeToken<MutableList<Usuario>>() {}.type)
        }
    }

    private fun signInGoogle() {
        binding.btnIngresarGoogle.setOnClickListener {
            //Configuración
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }
    }

    private fun signInFirebase() {
        binding.btnIngresar.setOnClickListener {
            if(camposValidos())
            {
                email = binding.etEmailLogin.text.toString()
                clave = binding.etClaveLogin.text.toString()
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,clave).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
                        prefs.putBoolean("borrar",false)
                        prefs.apply()
                        if(comprobarCorreoUsuario(it.result?.user?.email))
                        {
                            pasarPantallaHome(
                                HomeActivity::class.java,it.result?.user?.email,
                                ProviderType.BASIC
                            )
                        }
                    }
                    else
                    {
                        showAlert("Error","Se ha producido un error al autenticar al usuario")
                    }
                }
            }
            else
            {
                showAlert("Error","Algún campo no ha sido llenado")
            }
        }
    }

    private fun comprobarCorreoUsuario(email: String?): Boolean {
        listaUsuarios.forEach {
            if(it.correo.equals(email))
                return true
        }
        showAlert("Error","Su correo no esta registrado en el sistema. Por favor regístrese en la app")
        return false
    }


    private fun showAlert(titulo:String,mensaje:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun camposValidos(): Boolean {
        return binding.etEmailLogin.text.isNotEmpty() && binding.etClaveLogin.text.toString().isNotEmpty()
    }
    private fun pasarPantallaHome(actividad: Class<*>,email: String?,provider: ProviderType) {
        val intencion = Intent(applicationContext,actividad).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(intencion)
    }
    private fun pasarPantalla(actividad: Class<*>) {
        val intencion = Intent(applicationContext,actividad)
        startActivity(intencion)
        finish()
    }
    private fun hideStatusBar() {
        supportActionBar?.hide()
    }
    //Esta función se ejecuta cuando ha concluido la autenticación por google
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null)
                {
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            if(comprobarCorreoUsuario(account.email))
                            {
                                pasarPantallaHome(
                                    HomeActivity::class.java,
                                    account.email?: "",
                                    ProviderType.GOOGLE
                                )
                            }
                        } else {
                            showAlert("Error", "Se ha producido un error al autenticar al usuario")
                        }
                    }
                }
            }
            catch (e: ApiException){
                if(e.message.toString().substring(0,2) != "10")
                {
                    if (e.message.toString().substring(0, 5) == "12501")
                        showAlert(
                            "Advertencia",
                            "Debe confirmar una cuenta de google para poder proceder"
                        )
                }
                else
                    showAlert("Error","No se pudo establecer la conexión con el servicio de Google")
            }
        }
    }
}
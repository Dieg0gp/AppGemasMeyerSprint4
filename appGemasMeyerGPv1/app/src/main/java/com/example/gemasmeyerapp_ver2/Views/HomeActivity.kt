package com.example.gemasmeyerapp_ver2.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gemasmeyerapp_ver2.R
import com.example.gemasmeyerapp_ver2.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

enum class ProviderType {
    BASIC,
    GOOGLE
}
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navigationBottom: BottomNavigationView
    private var datos: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
        //setUp datos recibidos desde el login o registro
        //recuperar datos,guardarlos y mostrarlos
        recoverData()
        navigationViewConfig()
    }

    private fun navigationViewConfig() {
        navigationBottom = binding.bottomNavView
        val navigationController = findNavController(R.id.fragment)
        navigationBottom.setupWithNavController(navigationController)
    }

    private fun recoverData() {
        datos = intent.extras
        val email = datos?.getString("email")
        val provider = datos?.getString("provider")
        //si los valores no existen ponemos dos cadenas vacias
        mostrarDatosLogin(email?:"",provider?:"")
        //Guardar datos en almacenamiento local
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()
    }
    private fun pasarPantalla(actividad: Class<*>) {
        val intencion = Intent(applicationContext,actividad)
        startActivity(intencion)
        finish()
    }
    private fun mostrarDatosLogin(correo:String,proveedor:String) {
        Toast.makeText(this,"Correo $correo | provider $proveedor",Toast.LENGTH_LONG).show()
        val textEmail = findViewById<TextView>(R.id.txtEmail)
    }
    private fun hideStatusBar() {
        supportActionBar?.hide()
    }
}
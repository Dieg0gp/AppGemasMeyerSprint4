package com.example.gemasmeyerapp_ver2.Views

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gemasmeyerapp_ver2.Data.Constantes
import com.example.gemasmeyerapp_ver2.Permisos
import com.example.gemasmeyerapp_ver2.R
import com.example.gemasmeyerapp_ver2.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //comprobar permisos
        val permisos = Permisos(this)
        //si el dispositivo no esta conectado a internet muestra un mensaje
        if(!permisos.comprobarConexionInternet())
            permisos.mostrarConexionInternetRequerida()
        //cerrar sesión
        cerrarSesion()
        verificarLogin()
        hideStatusBar()
        uiSettings()
        fireBaseData()
        binding.btnInfo.setOnClickListener {
            Constantes.pasarPantalla(this,InfoActivity::class.java)
        }
        binding.btnRegistro.setOnClickListener {
            Constantes.pasarPantalla(this,RegistroActivity::class.java)
        }
        binding.btnLogin.setOnClickListener {
            Constantes.pasarPantalla(this,LoginActivity::class.java)
        }
    }



    private fun cerrarSesion() {
        val borrado = intent.extras
        val seBorra = borrado?.getBoolean("borrar")
        if(seBorra == true)
        {
            //Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun verificarLogin() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val proveedor = prefs.getString("provider",null)
        if(email != null && proveedor != null){
            Constantes.pasarPantallaHome(this,HomeActivity::class.java,email, ProviderType.valueOf(proveedor))
        }
    }

    private fun fireBaseData() {
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("mensaje","Integración de firebase completa")
        analytics.logEvent("InitScreen",bundle)
    }

    private fun uiSettings() {

    }

    private fun hideStatusBar() {
        supportActionBar?.hide()
    }
}
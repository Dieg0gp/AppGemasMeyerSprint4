package com.example.gemasmeyerapp_ver2.Views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.lifecycle.lifecycleScope
import com.example.gemasmeyerapp_ver2.AdaptadorProductos
import com.example.gemasmeyerapp_ver2.Data.Constantes
import com.example.gemasmeyerapp_ver2.Data.ProductoRepository
import com.example.gemasmeyerapp_ver2.Data.PublicacionesRepository
import com.example.gemasmeyerapp_ver2.DescargarImagenes
import com.example.gemasmeyerapp_ver2.Models.Producto
import com.example.gemasmeyerapp_ver2.Models.Publicacion
import com.example.gemasmeyerapp_ver2.PublicacionAdapter
import com.example.gemasmeyerapp_ver2.R
import com.example.gemasmeyerapp_ver2.databinding.ActivityPublicacionesBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class PublicacionesActivity : AppCompatActivity() {
    private lateinit var repositorioPublicaciones : PublicacionesRepository
    private lateinit var listaPublicaciones: MutableList<Publicacion>
    val gson = Gson()
    private lateinit var binding: ActivityPublicacionesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityPublicacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnVolverAtras.setOnClickListener {
            finish()
        }
        obtenerPublicaciones()
    }

    private fun obtenerPublicaciones() {
        val contexto = this
        repositorioPublicaciones = PublicacionesRepository()
        lifecycleScope.launch {
            val publicacionesJson = repositorioPublicaciones.obtenerPublicaciones()
            listaPublicaciones = gson.fromJson(publicacionesJson, object : TypeToken<MutableList<Publicacion>>() {}.type)
            val adapter = PublicacionAdapter(contexto, R.layout.item_publicacion, listaPublicaciones)
            binding.listViewPublicaciones.adapter = adapter
        }
    }
}
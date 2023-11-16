package com.example.gemasmeyerapp_ver2.Views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gemasmeyerapp_ver2.Data.Constantes
import com.example.gemasmeyerapp_ver2.databinding.ActivityInfoBinding

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
        binding.btnAtras.setOnClickListener {
            Constantes.pasarPantalla(this,MainActivity::class.java)
        }
    }

    private fun hideStatusBar() {
        supportActionBar?.hide()
    }
}
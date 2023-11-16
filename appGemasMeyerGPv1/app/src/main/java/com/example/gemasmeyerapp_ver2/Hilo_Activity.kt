package com.example.gemasmeyerapp_ver2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.gemasmeyerapp_ver2.Views.MainActivity
import com.example.gemasmeyerapp_ver2.databinding.ActivityHiloBinding

class Hilo_Activity : AppCompatActivity() {
    lateinit var binding: ActivityHiloBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiloBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 9000)
    }
}
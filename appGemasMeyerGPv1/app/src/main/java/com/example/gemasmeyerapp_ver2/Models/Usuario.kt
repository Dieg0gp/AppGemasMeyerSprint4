package com.example.gemasmeyerapp_ver2.Models

data class Usuario (val ci: String,
                    val clave: String,
                    val correo: String,
                    val celular: String,
                    val tipo: Int?,
                    val estado: Int,
                    val nombreCompleto: String)
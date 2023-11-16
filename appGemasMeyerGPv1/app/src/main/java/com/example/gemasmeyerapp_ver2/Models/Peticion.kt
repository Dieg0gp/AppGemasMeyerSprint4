package com.example.gemasmeyerapp_ver2.Models

data class Peticion (val idPeticion: Int? = null,val idUsuario: String,val productoNombre: String,val imagen: String,val cantidad: Int,val especificaciones: String,val estado: Int)
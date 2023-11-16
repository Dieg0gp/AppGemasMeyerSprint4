package com.example.gemasmeyerapp_ver2.Models

data class Publicacion (val idPublicacion: Int,
                     val titulo: String,
                     val descripcion: String,
                     val imagen: String,
                     val estado: Int,
                     val tipo: String,
                     val idProducto: Int? = null,
                     val descuento: Int? = null)
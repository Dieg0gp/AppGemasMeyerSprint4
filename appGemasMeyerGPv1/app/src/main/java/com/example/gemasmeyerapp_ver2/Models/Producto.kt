package com.example.gemasmeyerapp_ver2.Models

import android.graphics.Bitmap

data class Producto (val idProducto: Int,
                     val nombre: String,
                     val descripcion: String? = null,
                     val precio: Int,
                     val cantidad: Int,
                     val imagen: String,
                     var imagenRepuesto: Bitmap?,
                     val estado: Int)
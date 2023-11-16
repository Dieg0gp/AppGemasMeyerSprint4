package com.example.gemasmeyerapp_ver2.Data

import com.google.gson.JsonArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductoRepository : solicitudesProductos {

    private val productoServicio: solicitudesProductos

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constantes.URL_API) // Reemplaza con la URL base de tu API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        productoServicio = retrofit.create(solicitudesProductos::class.java)
    }

    override suspend fun obtenerProductos(): JsonArray {
        return productoServicio.obtenerProductos()
    }
    override suspend fun obtenerProductosEnStock(): JsonArray {
        return productoServicio.obtenerProductosEnStock()
    }
}

package com.example.gemasmeyerapp_ver2.Data

import com.google.gson.JsonArray
import retrofit2.http.GET

interface solicitudesProductos {
    @GET("Producto/ObtenerProductos")
    suspend fun obtenerProductos(): JsonArray
    @GET("Producto/ObtenerProductosEnStock")
    suspend fun obtenerProductosEnStock(): JsonArray
}